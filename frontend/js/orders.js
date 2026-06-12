let page = 0, totalPages = 1, products = [], customers = [];
const canManage = Auth.can('ADMIN', 'SALES_MANAGER');
const STATUSES = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

async function preload() {
  const [p, c] = await Promise.all([
    API.get('/products?size=200'), API.get('/customers?size=200')
  ]);
  products = p.content || []; customers = c.content || [];
  document.getElementById('customerId').innerHTML = customers.map(c => `<option value="${c.id}">${c.fullName}</option>`).join('');
}

async function load() {
  const status = document.getElementById('statusFilter').value;
  const params = new URLSearchParams({ page, size: 10 });
  if (status) params.set('status', status);
  try {
    const data = await API.get('/orders?' + params.toString());
    totalPages = data.totalPages || 1;
    renderRows(data.content || []);
    document.getElementById('pager').innerHTML = `
      <button ${page <= 0 ? 'disabled' : ''} onclick="page--;load()">Prev</button>
      <span class="muted">Page ${page + 1} of ${totalPages}</span>
      <button ${page >= totalPages - 1 ? 'disabled' : ''} onclick="page++;load()">Next</button>`;
  } catch (e) { UI.toast(e.message, 'error'); }
}

function renderRows(items) {
  const tbody = document.getElementById('rows');
  if (!items.length) { tbody.innerHTML = '<tr><td colspan="7"><div class="empty">No orders found</div></td></tr>'; return; }
  tbody.innerHTML = items.map(o => {
    const statusSelect = canManage && o.status !== 'CANCELLED' && o.status !== 'DELIVERED'
      ? `<select class="form-control btn-sm" style="padding:4px" onchange="updateStatus(${o.id}, this.value)">${STATUSES.map(s => `<option ${s === o.status ? 'selected' : ''}>${s}</option>`).join('')}</select>`
      : UI.statusBadge(o.status);
    return `<tr>
      <td><a href="#" onclick='viewOrder(${JSON.stringify(o)});return false' style="color:var(--accent);font-weight:600">${o.orderNumber}</a></td>
      <td>${o.customerName}</td>
      <td>${(o.items || []).length}</td>
      <td>${UI.money(o.totalAmount)}</td>
      <td>${statusSelect}</td>
      <td class="muted">${(o.createdAt || '').replace('T', ' ').slice(0, 16)}</td>
      <td style="text-align:right">${canManage && o.status !== 'CANCELLED' && o.status !== 'DELIVERED' ? `<button class="btn btn-sm btn-danger" onclick="cancelOrder(${o.id})">Cancel</button>` : ''}</td>
    </tr>`; }).join('');
}

function viewOrder(o) {
  document.getElementById('detailBody').innerHTML = `
    <h2>${o.orderNumber} ${UI.statusBadge(o.status)}</h2>
    <p class="muted">${o.customerName} \u00B7 ${(o.createdAt || '').replace('T', ' ').slice(0, 16)}</p>
    <table class="mt-4"><thead><tr><th>Product</th><th>Qty</th><th>Unit</th><th>Total</th></tr></thead>
      <tbody>${(o.items || []).map(i => `<tr><td>${i.productName}</td><td>${i.quantity}</td><td>${UI.money(i.unitPrice)}</td><td>${UI.money(i.lineTotal)}</td></tr>`).join('')}</tbody>
    </table>
    <h3 class="mt-4">Grand total: ${UI.money(o.totalAmount)}</h3>`;
  UI.openModal('detail');
}

async function updateStatus(id, status) {
  try { await API.patch('/orders/' + id + '/status', { status }); UI.toast('Status updated'); load(); }
  catch (e) { UI.toast(e.message, 'error'); load(); }
}
async function cancelOrder(id) {
  if (!confirm('Cancel this order? Stock will be restored.')) return;
  try { await API.post('/orders/' + id + '/cancel'); UI.toast('Order cancelled'); load(); }
  catch (e) { UI.toast(e.message, 'error'); }
}

function lineRow() {
  const div = document.createElement('div');
  div.className = 'form-row line';
  div.innerHTML = `
    <div class="form-group"><select class="form-control prod">${products.map(p => `<option value="${p.id}" data-price="${p.price}">${p.name} (${UI.money(p.price)}, stock ${p.stockQuantity})</option>`).join('')}</select></div>
    <div class="form-group" style="display:flex;gap:8px"><input class="form-control qty" type="number" min="1" value="1"><button type="button" class="btn btn-danger btn-sm" onclick="this.closest('.line').remove();recalc()">\u00D7</button></div>`;
  div.querySelector('.qty').addEventListener('input', recalc);
  div.querySelector('.prod').addEventListener('change', recalc);
  document.getElementById('lines').appendChild(div);
  recalc();
}
function recalc() {
  let total = 0;
  document.querySelectorAll('#lines .line').forEach(l => {
    const price = Number(l.querySelector('.prod').selectedOptions[0].dataset.price);
    const qty = Number(l.querySelector('.qty').value || 0);
    total += price * qty;
  });
  document.getElementById('estTotal').textContent = UI.money(total);
}

document.getElementById('form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const items = [...document.querySelectorAll('#lines .line')].map(l => ({
    productId: Number(l.querySelector('.prod').value),
    quantity: Number(l.querySelector('.qty').value)
  }));
  if (!items.length) { UI.toast('Add at least one item', 'warning'); return; }
  try {
    await API.post('/orders', { customerId: Number(customerId.value), items });
    UI.toast('Order created'); UI.closeModal('modal'); load();
  } catch (err) { UI.toast(err.message, 'error'); }
});

document.getElementById('addBtn').onclick = () => {
  document.getElementById('lines').innerHTML = ''; lineRow(); UI.openModal('modal');
};
if (!Auth.can('ADMIN', 'SALES_MANAGER', 'EMPLOYEE')) document.getElementById('addBtn').style.display = 'none';
document.getElementById('addLine').onclick = lineRow;
document.getElementById('statusFilter').onchange = () => { page = 0; load(); };
(async () => { await preload(); load(); })();

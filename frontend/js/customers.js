let page = 0, totalPages = 1;
const canDelete = Auth.can('ADMIN', 'SALES_MANAGER');

async function load() {
  const q = document.getElementById('search').value.trim();
  const status = document.getElementById('statusFilter').value;
  const params = new URLSearchParams({ page, size: 10 });
  if (q) params.set('search', q);
  if (status) params.set('status', status);
  try {
    const data = await API.get('/customers?' + params.toString());
    totalPages = data.totalPages || 1;
    renderRows(data.content || []);
    renderPager();
  } catch (e) { UI.toast(e.message, 'error'); }
}

function renderRows(items) {
  const tbody = document.getElementById('rows');
  if (!items.length) { tbody.innerHTML = '<tr><td colspan="8"><div class="empty">No customers found</div></td></tr>'; return; }
  tbody.innerHTML = items.map(c => `
    <tr>
      <td>#${c.id}</td>
      <td><a href="#" onclick="viewProfile(${c.id});return false" style="color:var(--accent);font-weight:600">${c.fullName}</a></td>
      <td>${c.email}</td>
      <td>${c.phoneNumber || '-'}</td>
      <td>${UI.money(c.totalPurchases)}</td>
      <td>${c.loyaltyPoints} pts</td>
      <td>${UI.statusBadge(c.status)}</td>
      <td style="text-align:right;white-space:nowrap">
        <button class="btn btn-sm btn-secondary" onclick='editCustomer(${JSON.stringify(c)})'>Edit</button>
        ${canDelete ? `<button class="btn btn-sm btn-danger" onclick="removeCustomer(${c.id})">Delete</button>` : ''}
      </td>
    </tr>`).join('');
}

function renderPager() {
  document.getElementById('pager').innerHTML = `
    <button ${page <= 0 ? 'disabled' : ''} onclick="page--;load()">Prev</button>
    <span class="muted">Page ${page + 1} of ${totalPages}</span>
    <button ${page >= totalPages - 1 ? 'disabled' : ''} onclick="page++;load()">Next</button>`;
}

function openAdd() {
  document.getElementById('form').reset();
  document.getElementById('cid').value = '';
  document.getElementById('modalTitle').textContent = 'Add customer';
  UI.openModal('modal');
}
function editCustomer(c) {
  document.getElementById('cid').value = c.id;
  fullName.value = c.fullName; email.value = c.email;
  phoneNumber.value = c.phoneNumber || ''; address.value = c.address || '';
  status.value = c.status;
  document.getElementById('modalTitle').textContent = 'Edit customer';
  UI.openModal('modal');
}
async function removeCustomer(id) {
  if (!confirm('Delete this customer?')) return;
  try { await API.del('/customers/' + id); UI.toast('Customer deleted'); load(); }
  catch (e) { UI.toast(e.message, 'error'); }
}
async function viewProfile(id) {
  try {
    const c = await API.get('/customers/' + id);
    const hist = await API.get('/customers/' + id + '/orders?size=5');
    const orders = (hist.content || []);
    document.getElementById('profileBody').innerHTML = `
      <h2>${c.fullName} ${UI.statusBadge(c.status)}</h2>
      <p class="muted">${c.email} \u00B7 ${c.phoneNumber || 'no phone'}</p>
      <div class="stats-grid" style="margin-top:16px">
        <div class="stat-card"><div class="label">Total purchases</div><div class="value">${UI.money(c.totalPurchases)}</div></div>
        <div class="stat-card"><div class="label">Loyalty points</div><div class="value">${c.loyaltyPoints}</div></div>
        <div class="stat-card"><div class="label">Registered</div><div class="value" style="font-size:16px">${c.registrationDate}</div></div>
      </div>
      <h3 class="mt-4">Purchase history</h3>
      ${orders.length ? orders.map(o => `<div style="display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid var(--border)"><span>${o.orderNumber} ${UI.statusBadge(o.status)}</span><strong>${UI.money(o.totalAmount)}</strong></div>`).join('') : '<p class="muted">No orders yet</p>'}`;
    UI.openModal('profile');
  } catch (e) { UI.toast(e.message, 'error'); }
}

document.getElementById('form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('cid').value;
  const body = { fullName: fullName.value, email: email.value, phoneNumber: phoneNumber.value, address: address.value, status: status.value };
  try {
    if (id) await API.put('/customers/' + id, body);
    else await API.post('/customers', body);
    UI.toast('Customer saved'); UI.closeModal('modal'); load();
  } catch (err) { UI.toast(err.message, 'error'); }
});

async function exportCsv() {
  try {
    const res = await API.raw('/customers/export');
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = 'customers.csv'; a.click();
    URL.revokeObjectURL(url);
  } catch (e) { UI.toast(e.message, 'error'); }
}

document.getElementById('addBtn').onclick = openAdd;
document.getElementById('exportBtn').onclick = exportCsv;
document.getElementById('search').addEventListener('input', UI.debounce(() => { page = 0; load(); }));
document.getElementById('statusFilter').onchange = () => { page = 0; load(); };
load();

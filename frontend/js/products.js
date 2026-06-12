let page = 0, totalPages = 1, categories = [];
const canEdit = Auth.can('ADMIN', 'SALES_MANAGER');
const canDelete = Auth.can('ADMIN');

async function loadCategories() {
  categories = await API.get('/categories');
  const opts = '<option value="">No category</option>' + categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
  document.getElementById('categoryId').innerHTML = opts;
  document.getElementById('catFilter').innerHTML = '<option value="">All categories</option>' + categories.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
}

async function load() {
  const q = document.getElementById('search').value.trim();
  const cat = document.getElementById('catFilter').value;
  const params = new URLSearchParams({ page, size: 10 });
  if (q) params.set('search', q);
  if (cat) params.set('categoryId', cat);
  try {
    const data = await API.get('/products?' + params.toString());
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
  if (!items.length) { tbody.innerHTML = '<tr><td colspan="8"><div class="empty">No products found</div></td></tr>'; return; }
  tbody.innerHTML = items.map(p => {
    const low = p.stockQuantity <= (p.lowStockThreshold || 10);
    return `<tr>
      <td>#${p.id}</td>
      <td>${p.name}</td>
      <td>${p.categoryName || '-'}</td>
      <td>${p.size || '-'}</td>
      <td>${p.color || '-'}</td>
      <td>${UI.money(p.price)}</td>
      <td>${p.stockQuantity} ${low ? '<span class="badge red">Low</span>' : ''}</td>
      <td style="text-align:right;white-space:nowrap">
        ${canEdit ? `<button class="btn btn-sm btn-secondary" onclick='editP(${JSON.stringify(p)})'>Edit</button>` : ''}
        ${canDelete ? `<button class="btn btn-sm btn-danger" onclick="removeP(${p.id})">Delete</button>` : ''}
      </td>
    </tr>`; }).join('');
}

function openAdd() {
  document.getElementById('form').reset();
  document.getElementById('pid').value = '';
  document.getElementById('modalTitle').textContent = 'Add product';
  UI.openModal('modal');
}
function editP(p) {
  pid.value = p.id; name.value = p.name; categoryId.value = p.categoryId || '';
  price.value = p.price; size.value = p.size || ''; color.value = p.color || '';
  stockQuantity.value = p.stockQuantity; lowStockThreshold.value = p.lowStockThreshold || 10;
  imageUrl.value = p.imageUrl || ''; description.value = p.description || '';
  document.getElementById('modalTitle').textContent = 'Edit product';
  UI.openModal('modal');
}
async function removeP(id) {
  if (!confirm('Delete this product?')) return;
  try { await API.del('/products/' + id); UI.toast('Product deleted'); load(); }
  catch (e) { UI.toast(e.message, 'error'); }
}

document.getElementById('form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = pid.value;
  const body = {
    name: name.value, categoryId: categoryId.value ? Number(categoryId.value) : null,
    price: Number(price.value), size: size.value, color: color.value,
    stockQuantity: Number(stockQuantity.value), lowStockThreshold: Number(lowStockThreshold.value || 10),
    imageUrl: imageUrl.value, description: description.value
  };
  try {
    if (id) await API.put('/products/' + id, body);
    else await API.post('/products', body);
    UI.toast('Product saved'); UI.closeModal('modal'); load();
  } catch (err) { UI.toast(err.message, 'error'); }
});

document.getElementById('addBtn').onclick = openAdd;
if (!canEdit) document.getElementById('addBtn').style.display = 'none';
document.getElementById('search').addEventListener('input', UI.debounce(() => { page = 0; load(); }));
document.getElementById('catFilter').onchange = () => { page = 0; load(); };
(async () => { await loadCategories(); load(); })();

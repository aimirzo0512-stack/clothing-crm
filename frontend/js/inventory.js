(async function () {
  try {
    const low = await API.get('/inventory/low-stock');
    const all = await API.get('/products?size=1');
    document.getElementById('invStats').innerHTML = `
      <div class="stat-card"><div class="icon blue">\u{1F4E6}</div><div class="label">Total SKUs</div><div class="value">${all.totalElements ?? 0}</div></div>
      <div class="stat-card"><div class="icon red" style="background:#fee2e2;color:#dc2626">\u26A0\uFE0F</div><div class="label">Low stock items</div><div class="value">${low.length}</div></div>`;
    const tbody = document.getElementById('rows');
    tbody.innerHTML = low.length ? low.map(p => `
      <tr>
        <td>${p.name}</td>
        <td>${p.categoryName || '-'}</td>
        <td><strong style="color:var(--danger)">${p.stockQuantity}</strong></td>
        <td>${p.lowStockThreshold}</td>
        <td><span class="badge red">Reorder</span></td>
      </tr>`).join('') : '<tr><td colspan="5"><div class="empty">All products are sufficiently stocked \u2705</div></td></tr>';
  } catch (e) { UI.toast(e.message, 'error'); }
})();

let salesChart;

async function loadSales() {
  const period = document.getElementById('period').value;
  try {
    const r = await API.get('/reports/sales?period=' + period);
    const labels = r.buckets.map(b => b.label);
    const revenue = r.buckets.map(b => b.revenue);
    const orders = r.buckets.map(b => b.orders);
    if (salesChart) salesChart.destroy();
    salesChart = new Chart(document.getElementById('salesChart'), {
      type: 'bar',
      data: { labels, datasets: [
        { label: 'Revenue ($)', data: revenue, backgroundColor: '#4f46e5', borderRadius: 6, yAxisID: 'y' },
        { label: 'Orders', type: 'line', data: orders, borderColor: '#16a34a', tension: .35, yAxisID: 'y1' }
      ] },
      options: { responsive: true, scales: {
        y: { position: 'left', title: { display: true, text: 'Revenue ($)' } },
        y1: { position: 'right', grid: { drawOnChartArea: false }, title: { display: true, text: 'Orders' } }
      } }
    });
  } catch (e) { UI.toast(e.message, 'error'); }
}

async function loadTop() {
  try {
    const top = await API.get('/reports/top-products');
    document.getElementById('topProducts').innerHTML = top.length ? top.map(p => `
      <tr><td>${p.productName}</td><td>${p.unitsSold}</td><td>${UI.money(p.revenue)}</td></tr>`).join('')
      : '<tr><td colspan="3"><div class="empty">No sales yet</div></td></tr>';
  } catch (e) { UI.toast(e.message, 'error'); }
}

async function loadCustomers() {
  try {
    const a = await API.get('/reports/customer-analytics');
    document.getElementById('customerAnalytics').innerHTML = `
      <div style="display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid var(--border)"><span>Total customers</span><strong>${a.totalCustomers}</strong></div>
      <div style="display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid var(--border)"><span>New this month</span><strong>${a.newThisMonth}</strong></div>
      <div style="display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid var(--border)"><span>VIP customers</span><strong>${a.vipCount}</strong></div>
      <div style="display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid var(--border)"><span>Avg. customer value</span><strong>${UI.money(a.averageCustomerValue)}</strong></div>
      <h3 class="mt-4">Top customers</h3>
      ${(a.topCustomers || []).map(c => `<div style="display:flex;justify-content:space-between;padding:6px 0"><span>${c.fullName}</span><strong>${UI.money(c.totalPurchases)}</strong></div>`).join('') || '<p class="muted">No data</p>'}`;
  } catch (e) { UI.toast(e.message, 'error'); }
}

document.getElementById('period').onchange = loadSales;
loadSales(); loadTop(); loadCustomers();

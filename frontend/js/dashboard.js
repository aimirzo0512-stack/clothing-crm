(async function () {
  try {
    const s = await API.get('/dashboard/stats');
    const cards = [
      ['Total Customers', s.totalCustomers, 'indigo', '\u{1F465}'],
      ['Total Orders', s.totalOrders, 'blue', '\u{1F6D2}'],
      ['Total Products', s.totalProducts, 'amber', '\u{1F455}'],
      ['Monthly Revenue', UI.money(s.monthlyRevenue), 'green', '\u{1F4B0}']
    ];
    document.getElementById('stats').innerHTML = cards.map(([label, value, color, icon]) => `
      <div class="stat-card">
        <div class="icon ${color}">${icon}</div>
        <div class="label">${label}</div>
        <div class="value">${value}</div>
      </div>`).join('') + `
      <div class="stat-card">
        <div class="icon red" style="background:#fee2e2;color:#dc2626">\u26A0\uFE0F</div>
        <div class="label">Low Stock Items</div>
        <div class="value">${s.lowStockCount}</div>
      </div>`;

    // Recent activity list
    const act = s.recentActivities || [];
    document.getElementById('activity').innerHTML = act.length ? act.map(a => `
      <div style="display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid var(--border)">
        <div><strong>${a.action}</strong> \u00B7 ${a.entityType || ''}<br><span class="muted" style="font-size:12px">${a.details || ''}</span></div>
        <span class="muted" style="font-size:12px;white-space:nowrap">${a.timestamp}</span>
      </div>`).join('') : '<div class="empty">No recent activity</div>';

    // Sales chart
    const c = s.salesChart || { labels: [], revenue: [], orders: [] };
    new Chart(document.getElementById('salesChart'), {
      type: 'line',
      data: {
        labels: c.labels,
        datasets: [
          { label: 'Revenue', data: c.revenue, borderColor: '#4f46e5', backgroundColor: 'rgba(79,70,229,.1)', fill: true, tension: .35, yAxisID: 'y' },
          { label: 'Orders', data: c.orders, borderColor: '#16a34a', backgroundColor: 'rgba(22,163,74,.1)', tension: .35, yAxisID: 'y1' }
        ]
      },
      options: {
        responsive: true,
        interaction: { mode: 'index', intersect: false },
        scales: {
          y: { position: 'left', title: { display: true, text: 'Revenue ($)' } },
          y1: { position: 'right', grid: { drawOnChartArea: false }, title: { display: true, text: 'Orders' } }
        }
      }
    });
  } catch (e) { UI.toast(e.message, 'error'); }
})();

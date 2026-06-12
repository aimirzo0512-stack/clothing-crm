/* Reusable UI helpers: toasts, modals, layout (sidebar/topbar), formatting. */
const UI = {
  toast(message, type = 'success') {
    let c = document.getElementById('toast-container');
    if (!c) { c = document.createElement('div'); c.id = 'toast-container'; document.body.appendChild(c); }
    const t = document.createElement('div');
    t.className = 'toast ' + type;
    t.textContent = message;
    c.appendChild(t);
    setTimeout(() => { t.style.opacity = '0'; setTimeout(() => t.remove(), 300); }, 3200);
  },

  money(v) {
    const n = Number(v || 0);
    return '$' + n.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  },

  statusBadge(status) {
    const map = {
      ACTIVE: 'green', VIP: 'purple', INACTIVE: 'gray', BLOCKED: 'red',
      PENDING: 'amber', PROCESSING: 'blue', SHIPPED: 'blue', DELIVERED: 'green', CANCELLED: 'red'
    };
    return `<span class="badge ${map[status] || 'gray'}">${status}</span>`;
  },

  openModal(id) { document.getElementById(id).classList.add('open'); },
  closeModal(id) { document.getElementById(id).classList.remove('open'); },

  // Builds the shared sidebar + topbar chrome on protected pages.
  layout(active, title) {
    const u = Auth.user() || {};
    const initials = (u.fullName || u.username || '?').split(' ').map(s => s[0]).join('').slice(0,2).toUpperCase();
    const items = [
      ['dashboard.html', 'Dashboard', '\u{1F4CA}'],
      ['customers.html', 'Customers', '\u{1F465}'],
      ['products.html', 'Products', '\u{1F455}'],
      ['orders.html', 'Orders', '\u{1F6D2}'],
      ['inventory.html', 'Inventory', '\u{1F4E6}'],
      ['reports.html', 'Reports', '\u{1F4C8}']
    ];
    const nav = items.map(([href, label, icon]) =>
      `<a href="${href}" class="${active === href ? 'active' : ''}"><span>${icon}</span>${label}</a>`).join('');

    document.body.insertAdjacentHTML('afterbegin', `
      <div class="app">
        <aside class="sidebar" id="sidebar">
          <div class="logo"><span>\u{1F455}</span> StyleCRM</div>
          <nav>${nav}</nav>
          <div class="role-badge">Signed in as<br><strong style="color:#fff">${u.username || ''}</strong> \u00B7 ${u.role || ''}</div>
        </aside>
        <div class="main">
          <header class="topbar">
            <div style="display:flex;align-items:center;gap:12px">
              <button class="hamburger" onclick="document.getElementById('sidebar').classList.toggle('open')">\u2630</button>
              <span class="page-title">${title}</span>
            </div>
            <div class="user-menu">
              <div class="avatar">${initials}</div>
              <button class="btn btn-ghost btn-sm" onclick="Auth.logout()">Logout</button>
            </div>
          </header>
          <main class="content" id="content"></main>
        </div>
      </div>
      <div class="loading-overlay" id="loading-overlay"><div class="spinner"></div></div>
      <div id="toast-container"></div>
    `);
  },

  debounce(fn, ms = 350) {
    let h; return (...a) => { clearTimeout(h); h = setTimeout(() => fn(...a), ms); };
  }
};

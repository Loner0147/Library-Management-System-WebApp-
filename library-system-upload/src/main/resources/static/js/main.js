/* ===================================
   LibraryHub — main.js
   Shared utility functions
   =================================== */

// Toggle user dropdown
function toggleUserMenu() {
  const dropdown = document.getElementById('userDropdown');
  if (dropdown) dropdown.classList.toggle('open');
}

// Close dropdown on outside click
document.addEventListener('click', (e) => {
  const menu = document.getElementById('userMenu');
  const dropdown = document.getElementById('userDropdown');
  if (menu && dropdown && !menu.contains(e.target)) {
    dropdown.classList.remove('open');
  }
});

// Toggle password visibility
function togglePassword(inputId) {
  const input = document.getElementById(inputId);
  if (!input) return;
  input.type = input.type === 'password' ? 'text' : 'password';
}

// Password strength indicator
const passwordInput = document.getElementById('password');
if (passwordInput) {
  passwordInput.addEventListener('input', () => {
    const val = passwordInput.value;
    const bar = document.getElementById('strengthBar');
    if (!bar) return;
    bar.className = 'password-strength';
    if (val.length < 8) return;
    const hasUpper = /[A-Z]/.test(val);
    const hasNumber = /\d/.test(val);
    const hasSpecial = /[^A-Za-z0-9]/.test(val);
    const score = [val.length >= 12, hasUpper, hasNumber, hasSpecial].filter(Boolean).length;
    if (score <= 2) bar.classList.add('strength-weak');
    else if (score === 3) bar.classList.add('strength-medium');
    else bar.classList.add('strength-strong');
  });
}

// ===== Toast Notification =====
function showToast(message, type = 'info') {
  const toast = document.getElementById('toast');
  if (!toast) return;
  toast.textContent = message;
  toast.className = `toast toast-${type} show`;
  setTimeout(() => { toast.classList.remove('show'); }, 3500);
}

// ===== Register form handler =====
async function handleRegister(e) {
  e.preventDefault();
  const firstName = document.getElementById('firstName').value.trim();
  const lastName = document.getElementById('lastName').value.trim();
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;
  const confirmPassword = document.getElementById('confirmPassword').value;

  const alertBox = document.getElementById('alertBox');
  const showAlert = (msg, type) => {
    alertBox.textContent = msg;
    alertBox.className = `alert alert-${type}`;
    alertBox.style.display = 'block';
  };

  if (password !== confirmPassword) {
    showAlert('Passwords do not match.', 'error');
    return;
  }

  const btn = document.getElementById('submitBtn');
  btn.disabled = true;
  btn.textContent = 'Creating account...';

  try {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ firstName, lastName, email, password })
    });
    const data = await res.json();
    if (res.ok) {
      window.location.href = '/login?registered=true';
    } else {
      showAlert(data.message || 'Registration failed. Please try again.', 'error');
      btn.disabled = false;
      btn.textContent = 'Create Account';
    }
  } catch (err) {
    showAlert('Network error. Please try again.', 'error');
    btn.disabled = false;
    btn.textContent = 'Create Account';
  }
}

// ===== Tabs =====
function showTab(tabId) {
  document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
  const panel = document.getElementById(`tab-${tabId}`);
  if (panel) panel.classList.add('active');
  const activeBtn = [...document.querySelectorAll('.tab-btn')]
    .find(b => b.getAttribute('onclick')?.includes(tabId));
  if (activeBtn) activeBtn.classList.add('active');
}

// ===== CSRF helper for non-form requests =====
function getCsrfToken() {
  const meta = document.querySelector('meta[name="_csrf"]');
  return meta ? meta.getAttribute('content') : null;
}

function getCsrfHeader() {
  const meta = document.querySelector('meta[name="_csrf_header"]');
  return meta ? meta.getAttribute('content') : 'X-CSRF-TOKEN';
}

// For API calls from the same-domain browser context, the JWT cookie is sent automatically.
// These are fire-and-forget helpers.
async function apiRequest(url, method = 'GET', body = null) {
  const opts = {
    method,
    headers: { 'Content-Type': 'application/json' },
    credentials: 'same-origin'
  };
  if (body) opts.body = JSON.stringify(body);
  const res = await fetch(url, opts);
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.message || data.error || `HTTP ${res.status}`);
  return data;
}

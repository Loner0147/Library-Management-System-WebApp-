/* ===================================
   LibraryHub — admin.js
   Admin user management actions
   =================================== */

async function promoteUser(userId) {
  if (!confirm('Promote this user to Admin?')) return;
  try {
    await apiRequest(`/api/admin/users/${userId}/promote`, 'PUT');
    showToast('User promoted to Admin!', 'success');
    setTimeout(() => window.location.reload(), 1200);
  } catch (err) {
    showToast('Failed: ' + err.message, 'error');
  }
}

async function demoteUser(userId) {
  if (!confirm('Remove admin role from this user?')) return;
  try {
    await apiRequest(`/api/admin/users/${userId}/demote`, 'PUT');
    showToast('Admin role removed.', 'success');
    setTimeout(() => window.location.reload(), 1200);
  } catch (err) {
    showToast('Failed: ' + err.message, 'error');
  }
}

async function toggleUserStatus(userId) {
  try {
    await apiRequest(`/api/admin/users/${userId}/toggle-status`, 'PUT');
    showToast('User status updated.', 'success');
    setTimeout(() => window.location.reload(), 1200);
  } catch (err) {
    showToast('Failed: ' + err.message, 'error');
  }
}

async function deleteUser(userId) {
  if (!confirm('Permanently delete this user? This cannot be undone.')) return;
  try {
    await apiRequest(`/api/admin/users/${userId}`, 'DELETE');
    showToast('User deleted.', 'success');
    setTimeout(() => window.location.reload(), 1000);
  } catch (err) {
    showToast('Failed: ' + err.message, 'error');
  }
}

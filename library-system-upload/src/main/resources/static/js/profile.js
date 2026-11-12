/* ===================================
   LibraryHub — profile.js
   Profile update + password change
   =================================== */

async function updateProfile(e) {
  e.preventDefault();
  const payload = {
    firstName: document.getElementById('settingsFirstName').value.trim(),
    lastName: document.getElementById('settingsLastName').value.trim(),
    profileImageUrl: document.getElementById('settingsImageUrl').value.trim() || null
  };
  try {
    await apiRequest('/api/profile/update', 'PUT', payload);
    showToast('Profile updated!', 'success');
    setTimeout(() => window.location.reload(), 1200);
  } catch (err) {
    showToast('Failed to update: ' + err.message, 'error');
  }
}

async function changePassword(e) {
  e.preventDefault();
  const oldPassword = document.getElementById('oldPassword').value;
  const newPassword = document.getElementById('newPassword').value;
  if (newPassword.length < 8) {
    showToast('New password must be at least 8 characters.', 'error');
    return;
  }
  try {
    await apiRequest('/api/profile/change-password', 'PUT', { oldPassword, newPassword });
    showToast('Password changed!', 'success');
    document.getElementById('passwordForm').reset();
  } catch (err) {
    showToast('Failed: ' + err.message, 'error');
  }
}

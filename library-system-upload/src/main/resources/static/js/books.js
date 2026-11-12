/* ===================================
   LibraryHub — books.js
   Book CRUD + borrow/return actions
   =================================== */

// ===== MODAL OPEN/CLOSE =====
function openAddBookModal() {
  document.getElementById('modalTitle').textContent = 'Add New Book';
  document.getElementById('bookId').value = '';
  document.getElementById('bookForm').reset();
  document.getElementById('bookModal').classList.add('open');
}

function closeBookModal() {
  document.getElementById('bookModal').classList.remove('open');
}

// ===== OPEN EDIT MODAL =====
async function openEditModal(bookId) {
  try {
    const book = await apiRequest(`/api/books/${bookId}`);
    document.getElementById('modalTitle').textContent = 'Edit Book';
    document.getElementById('bookId').value = book.id;
    document.getElementById('bTitle').value = book.title || '';
    document.getElementById('bAuthor').value = book.author || '';
    document.getElementById('bIsbn').value = book.isbn || '';
    document.getElementById('bGenre').value = book.genre || '';
    document.getElementById('bPublisher').value = book.publisher || '';
    document.getElementById('bYear').value = book.publicationYear || '';
    document.getElementById('bLanguage').value = book.language || '';
    document.getElementById('bPages').value = book.pageCount || '';
    document.getElementById('bDescription').value = book.description || '';
    document.getElementById('bCoverUrl').value = book.coverImageUrl || '';
    document.getElementById('bDocUrl').value = book.documentUrl || '';
    document.getElementById('bCopies').value = book.totalCopies || 1;
    document.getElementById('bTags').value = book.tags ? book.tags.join(', ') : '';
    document.getElementById('bookModal').classList.add('open');
  } catch (err) {
    showToast('Failed to load book details: ' + err.message, 'error');
  }
}

// Alias for book detail page
function openEditBook(bookId) { openEditModal(bookId); }

// ===== SAVE BOOK =====
async function saveBook(e) {
  e.preventDefault();
  const bookId = document.getElementById('bookId').value;
  const tagsRaw = document.getElementById('bTags').value;
  const tags = tagsRaw ? tagsRaw.split(',').map(t => t.trim()).filter(Boolean) : [];

  const payload = {
    title: document.getElementById('bTitle').value.trim(),
    author: document.getElementById('bAuthor').value.trim(),
    isbn: document.getElementById('bIsbn').value.trim() || null,
    genre: document.getElementById('bGenre').value.trim() || null,
    publisher: document.getElementById('bPublisher').value.trim() || null,
    publicationYear: parseInt(document.getElementById('bYear').value) || null,
    language: document.getElementById('bLanguage').value.trim() || null,
    pageCount: parseInt(document.getElementById('bPages').value) || null,
    description: document.getElementById('bDescription').value.trim() || null,
    coverImageUrl: document.getElementById('bCoverUrl').value.trim() || null,
    documentUrl: document.getElementById('bDocUrl').value.trim() || null,
    totalCopies: parseInt(document.getElementById('bCopies').value) || 1,
    tags
  };

  const isEdit = !!bookId;
  const url = isEdit ? `/api/books/${bookId}` : '/api/books';
  const method = isEdit ? 'PUT' : 'POST';

  try {
    await apiRequest(url, method, payload);
    closeBookModal();
    showToast(isEdit ? 'Book updated successfully!' : 'Book added successfully!', 'success');
    setTimeout(() => window.location.reload(), 1200);
  } catch (err) {
    showToast('Error saving book: ' + err.message, 'error');
  }
}

// ===== DELETE BOOK =====
async function deleteBook(bookId) {
  if (!confirm('Are you sure you want to delete this book? This cannot be undone.')) return;
  try {
    await apiRequest(`/api/books/${bookId}`, 'DELETE');
    showToast('Book deleted.', 'success');
    setTimeout(() => window.location.reload(), 1000);
  } catch (err) {
    showToast('Failed to delete book: ' + err.message, 'error');
  }
}

async function deleteBookAndRedirect(bookId) {
  if (!confirm('Are you sure you want to delete this book?')) return;
  try {
    await apiRequest(`/api/books/${bookId}`, 'DELETE');
    showToast('Book deleted.', 'success');
    setTimeout(() => window.location.href = '/books', 1000);
  } catch (err) {
    showToast('Failed to delete book: ' + err.message, 'error');
  }
}

// ===== BORROW BOOK =====
async function borrowBook(bookId) {
  try {
    const res = await apiRequest(`/api/books/${bookId}/borrow`, 'POST');
    showToast(res.message || 'Book borrowed!', 'success');
    setTimeout(() => window.location.reload(), 1200);
  } catch (err) {
    showToast('Could not borrow: ' + err.message, 'error');
  }
}

// ===== RETURN BOOK =====
async function returnBook(bookId) {
  try {
    const res = await apiRequest(`/api/books/${bookId}/return`, 'POST');
    showToast(res.message || 'Book returned!', 'success');
    setTimeout(() => window.location.reload(), 1200);
  } catch (err) {
    showToast('Could not return: ' + err.message, 'error');
  }
}

// Close modal on Escape
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') closeBookModal();
});

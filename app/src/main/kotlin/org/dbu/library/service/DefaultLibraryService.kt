package org.dbu.library.service

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository

class DefaultLibraryService(private val repository: LibraryRepository) : LibraryService {

    private val borrowLimit = 3

    override fun addBook(book: Book): Boolean = repository.addBook(book)

    // registerPatron is not part of the interface, so no override
    fun registerPatron(patron: Patron): Boolean = repository.addPatron(patron)

    override fun borrowBook(patronId: String, isbn: String): BorrowResult {
        val patron = repository.findPatron(patronId) ?: return BorrowResult.PATRON_NOT_FOUND
        val book = repository.findBook(isbn) ?: return BorrowResult.BOOK_NOT_FOUND

        if (!book.isAvailable) return BorrowResult.NOT_AVAILABLE
        if (patron.borrowedBooks.size >= borrowLimit) return BorrowResult.LIMIT_REACHED

        // Update book availability
        val updatedBook = book.copy(isAvailable = false)
        // Add book to patron's borrowed books
        val updatedPatron = patron.copy(borrowedBooks = patron.borrowedBooks + isbn)

        repository.updateBook(updatedBook)
        repository.updatePatron(updatedPatron)
        return BorrowResult.SUCCESS
    }

    override fun returnBook(patronId: String, isbn: String): Boolean {
        val patron = repository.findPatron(patronId) ?: return false
        val book = repository.findBook(isbn) ?: return false
        if (!patron.borrowedBooks.contains(isbn)) return false

        // Update book availability
        val updatedBook = book.copy(isAvailable = true)
        // Remove book from patron's borrowed books
        val updatedPatron = patron.copy(borrowedBooks = patron.borrowedBooks - isbn)

        repository.updateBook(updatedBook)
        repository.updatePatron(updatedPatron)
        return true
    }

    override fun search(query: String): List<Book> {
        return repository.getAllBooks().filter {
            it.title.contains(query, ignoreCase = true) ||
            it.author.contains(query, ignoreCase = true)
        }
    }

    // Helper functions for UI (not overrides)
    fun listAllBooks(): List<Book> = repository.getAllBooks()
    fun listAllPatrons(): List<Patron> = repository.getAllPatrons()
}
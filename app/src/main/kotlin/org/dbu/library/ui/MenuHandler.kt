package org.dbu.library.ui

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.DefaultLibraryService
import org.dbu.library.service.LibraryService

// -----------------------------
// Handle menu selection
// -----------------------------
fun handleMenuAction(
    choice: String,
    service: DefaultLibraryService,
    repository: LibraryRepository
): Boolean {
    return when (choice) {

        "1" -> { addBook(service); true }
        "2" -> { registerPatron(service); true }
        "3" -> { borrowBook(service); true }
        "4" -> { returnBook(service); true }
        "5" -> { search(service); true }
        "6" -> { listAllBooks(service); true }
        "7" -> { listAllPatrons(service); true } // optional
        "0" -> false
        else -> { println("Invalid option"); true }
    }
}

// -----------------------------
// Add a new book
// -----------------------------
fun addBook(service: LibraryService) {
    println("Enter ISBN:")
    val isbn = readLine().orEmpty()
    println("Enter title:")
    val title = readLine().orEmpty()
    println("Enter author:")
    val author = readLine().orEmpty()
    println("Enter year:")
    val year = readLine()?.toIntOrNull() ?: 0

    val book = Book(isbn = isbn, title = title, author = author, year = year, isAvailable = true)
    if (service.addBook(book)) println("Book added successfully!")
    else println("Book already exists.")
}

// -----------------------------
// Register a new patron
// -----------------------------
fun registerPatron(service: DefaultLibraryService) {
    println("Enter Patron ID:")
    val id = readLine().orEmpty()
    println("Enter name:")
    val name = readLine().orEmpty()
    val patron = Patron(id = id, name = name)
    if (service.registerPatron(patron)) println("Patron added")
    else println("Patron already exists")
}

// -----------------------------
// Borrow a book
// -----------------------------
fun borrowBook(service: LibraryService) {
    println("Enter Patron ID:")
    val patronId = readLine().orEmpty()
    println("Enter Book ISBN:")
    val isbn = readLine().orEmpty()

    when (service.borrowBook(patronId, isbn)) {
        BorrowResult.SUCCESS -> println("Book borrowed successfully!")
        BorrowResult.PATRON_NOT_FOUND -> println("Patron not found.")
        BorrowResult.BOOK_NOT_FOUND -> println("Book not found.")
        BorrowResult.LIMIT_REACHED -> println("Patron has reached borrow limit.")
        BorrowResult.NOT_AVAILABLE -> println("Book is currently not available.")
    }
}

// -----------------------------
// Return a book
// -----------------------------
fun returnBook(service: LibraryService) {
    println("Enter Patron ID:")
    val patronId = readLine().orEmpty()
    println("Enter Book ISBN:")
    val isbn = readLine().orEmpty()

    if (service.returnBook(patronId, isbn)) println("Book returned successfully!")
    else println("Failed to return the book. Check Patron ID or ISBN.")
}

// -----------------------------
// Search books by title or author
// -----------------------------
fun search(service: LibraryService) {
    println("Enter search query (title or author):")
    val query = readLine().orEmpty()
    val results = service.search(query)
    if (results.isEmpty()) println("No books found matching \"$query\"")
    else {
        println("Found books:")
        results.forEach { book ->
            val status = if (book.isAvailable) "Available" else "Borrowed"
            println("${book.title} by ${book.author} [${book.isbn}] - $status")
        }
    }
}

// -----------------------------
// List all books
// -----------------------------
fun listAllBooks(service: DefaultLibraryService) {
    val books = service.listAllBooks()
    if (books.isEmpty()) println("No books in library.")
    else {
        println("Library Books:")
        books.forEach { book ->
            val status = if (book.isAvailable) "Available" else "Borrowed"
            println("${book.title} by ${book.author} [${book.isbn}] - $status")
        }
    }
}

// -----------------------------
// List all patrons
// -----------------------------
fun listAllPatrons(service: DefaultLibraryService) {
    val patrons = service.listAllPatrons()
    if (patrons.isEmpty()) println("No registered patrons.")
    else {
        println("Registered Patrons:")
        patrons.forEach { patron ->
            println("${patron.name} [${patron.id}] - Borrowed books: ${patron.borrowedBooks.joinToString()}")
        }
    }
}
function validateUsername() {
    const name = document.getElementById("username");
    if (name.value.length < 1) {
        alert("Username cannot be empty!");
        return false;
    } else {
        return true;
    }
}

function containsSpecialChar(str) {
    const pattern = new RegExp(/[~`!#$%\^&*+=\-\[\]\\';,/{}|\\":<>\?]/g);
    return pattern.test(str);
}

function containsDigit(str) {
    return /\d/.test(str);
}

function validateAuthorName() {
    const authorName = document.getElementById("name").value;
    if (!(authorName.split(" ").length >= 2)) {
        alert("Author name must have at least two parts!");
        return false;
    } else if (containsSpecialChar(authorName) || containsDigit(authorName)) {
        alert("Invalid character in author name!");
        return false;
    } else {
        return true;
    }
}

function validateIsbn() {
    const isbn = document.getElementById("isbn").value;
    if (/^\d{10}$/.test(isbn) || /^\d{3}-\d{10}$/.test(isbn)) {
        return true;
    } else {
        alert("Invalid ISBN!");
        return false;
    }
}
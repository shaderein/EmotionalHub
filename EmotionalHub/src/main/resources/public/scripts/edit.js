function analyzePostInSeparateRoute(form) {
    // enable this if model prediction takes too long
    //let textContent = form.children[0].value;
    fetch("/analyze-post", {
        method: "POST",
        /*
        body: JSON.stringify({content: textContent}),
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        }*/
    })/*.then(res => {
        console.log("Make post request complete! response:", res);
    })*/;
}

function openEditor(postID) {
    let toEdit = document.getElementById("popup");
    let view = toEdit.getElementsByClassName("view").item(0);
    let edit = toEdit.getElementsByClassName("edit").item(0);
    view.style.display = "none";
    view.style.opacity = "0";
    view.className = "view hidden";
    edit.style.display = "block";
    edit.style.opacity = "1";
    edit.className = "edit";
}

function closeEditor(postID) {
    let toEdit = document.getElementById("popup");
    let view = toEdit.getElementsByClassName("view").item(0);
    let edit = toEdit.getElementsByClassName("edit").item(0);
    view.style.display = "block";
    view.style.opacity = "1";
    view.className = "view";
    edit.style.display = "none";
    edit.style.opacity = "0";
    edit.className = "edit hidden";
}

function expand(id) {
    let popup = document.getElementById("popup");
    if (popup) { return; }
    let expander = document.getElementById(id);
    let clone = expander.cloneNode(true);
    clone.classList.add("expanded");
    clone.id = "popup";
    clone.removeAttribute("onclick");
    // Note: Post form has "shrunken" class
    let elems = clone.getElementsByClassName("shrunken");
    // List shrinks as elements are removed, do not increment
    for (let i = 0; i < elems.length; ) {
        let elem = elems.item(i);
        elem.style.display = "block";
        elem.style.opacity = "1";
        elem.classList.remove("shrunken");
    }

    expander.parentNode.appendChild(clone);
    setTimeout(() => document.addEventListener('click', popupClick), 10);
}

function popupClick(event) {
    let popup = document.getElementById("popup");
    if (!popup) { return; }
    if (event.target instanceof Node) {
        if (!popup.contains(event.target)) {
            popup.remove();
            document.removeEventListener('click', popupClick);
        }
    }
}

/*
// Toggle the display when hovered
$(".dislike-btn").hover(function() {
    $(this).children().addClass("fa-thumbs-down");
}, function() {
    $(this).children().removeClass("fa-thumbs-down");
});

$(".like-btn").hover(function(){
    $(this).children().toggleClass("fa-heart-o");
    $(this).children().addClass("fa-heart");
}, function() {
    $(this).children().addClass("fa-heart-o");
    $(this).children().removeClass("fa-heart");
});
*/

/**
 * Toggle whether the post given by the Id is liked.
 * @param e
 * @param postId
 */
function likePost(e, postId) {
    stopBubbling(e);

    let like = e.target.classList.contains("fa-heart-o");  // Whether the heart is empty.

    fetch("/add-favorite", {
        method: "POST",
        body: JSON.stringify({postId: postId, like: like.toString()}),
        headers: {
            'Content-type': 'application/json;'
        }
    }).then(res => {
        console.log("Added / removed one favorite post", res);
    });
    e.target.classList.toggle("fa-heart");
    e.target.classList.toggle("fa-heart-o");

    let numLikesSpan = e.target.previousElementSibling;
    numLikesSpan.innerHTML = like ?
        (parseInt(numLikesSpan.innerHTML) + 1).toString() : (parseInt(numLikesSpan.innerHTML) - 1).toString();
}

function dislikePost() {

}

// Stop the click event from bubbling up the DOM.
function stopBubbling(evt){
    evt.stopPropagation();
    evt.cancelBubble = true;
}
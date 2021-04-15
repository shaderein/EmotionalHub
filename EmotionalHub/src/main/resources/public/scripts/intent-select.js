function selectIntention(btn) {
    let id = btn.id;
    let intent;
    if (id === "excite" || id === "cheer-up") {
        intent = "excite";
    } else if (id === "inspire" || id === "motivate") {
        intent = "inspire";
    } else if (id === "chill" || id === "relax") {
        intent = "chill";
    } else {
        intent = id;
    }
    window.location.href = "/feed?intent=" + intent;
}
const form = document.getElementById("document-form");

for (let key in sessionStorage) {
    let inputField = document.querySelector(`input[name='${key}']`);
    if (inputField !== null) {
        inputField.value = sessionStorage.getItem(key);
    }
}

document.querySelectorAll("#document-form input").forEach(i => {
    i.oninput = (e) => {
        sessionStorage.setItem(i.name, i.value);
    };
})

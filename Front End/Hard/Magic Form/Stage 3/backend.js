// This is important
let keys = ["first-name", "last-name", "email", "phone", "company", "address"]

let getKey = (key) => {
    if (key === "first-name")
        return "firstName";
    else if (key === "last-name")
        return "lastName";
    return key;
};

for (let key of keys) {
    let inputField = document.querySelector(`input[name=${key}]`);
    inputField.value = localStorage.getItem(key) || '';
}

document.querySelectorAll("#document-form input").forEach(i => {
    i.oninput = () => {
        localStorage.setItem(i.name, i.value);
    };
});

document.querySelector("#document-form").onsubmit = e => {
    let object = {};

    // object.proper

    for (let i in keys) {
        object[getKey(keys[i])] = localStorage.getItem(keys[i]);
        localStorage.setItem(keys[i], '');
        document.querySelector(`input[name=${keys[i]}]`).value = '';
    }

    localStorage.setItem(String(localStorage.length), JSON.stringify(object));
};
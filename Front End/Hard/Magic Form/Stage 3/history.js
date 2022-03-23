let historyBlock = document.querySelector("#history-collection");
let keys = ["first-name", "last-name", "email", "phone", "company", "address"]

for (let key in localStorage) {
    if (!isNaN(Number(key))) {
        let object = JSON.parse(localStorage.getItem(key));
        historyBlock.innerHTML += `<div class="submit-history-card card">
                    <p class="card-first-name">
                        <span>First Name</span>
                        <span>${object.firstName}</span>
                    </p>
                    <p class="card-last-name">
                        <span>Last Name</span>
                        <span>${object.lastName}</span>
                    </p>
                    <p class="card-email">
                        <span>Email</span>
                        <span>${object.email}</span>
                    </p>
                    <p class="card-phone">
                        <span>Phone</span>
                        <span>${object.phone}</span>
                    </p>
                    <p class="card-company">
                        <span>Company</span>
                        <span>${object.company}</span>
                    </p>
                    <p class="card-address">
                        <span>Address</span>
                        <span>${object.address}</span>
                    </p>
                    <button class="delete-button btn btn-danger">Delete</button>
                </div>`;
    }
}
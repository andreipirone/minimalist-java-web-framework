const tableSelector = document.getElementById("table-body");

const getData = async () => {
    try {
        const response = await fetch("/all");

        if (!response.ok) {
            console.log(`${response.status}`);
        }

        const data = await response.json();
        populateTable(data);
    } catch (error) {
        console.error('Fetch error:', error);
    }
};

const populateTable = (data) => {
    tableSelector.innerHTML = data.map(el => `
        <tr>
            <td>${el.id}</td>
            <td>${el.firstName}</td>
            <td>${el.lastName}</td>
            <td>${el.age}</td>
<!--            <td><button>Edit</button>-->
<!--            <button>Delete</button></td>-->
        </tr>
    `).join('');
};

window.addEventListener("DOMContentLoaded", getData);
window.addEventListener("submit", window.location.reload);






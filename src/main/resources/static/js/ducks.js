import { postData, ready, yyyymmdd } from "./base.js";

function loadDucks() {
    const duckTable = document.getElementById("ducks");
    const previousRows = duckTable.querySelectorAll('tr:not(:first-child)');
    Array.prototype.forEach.call(previousRows, function(row) { row.remove(); });
    fetch('/api/ducks')
        .then(response => response.json())
        .then(data => {
            data.forEach(element => {
                let duckRow = duckTable.insertRow(-1);
                let duckNameCell = duckRow.insertCell(0);
                duckNameCell.appendChild(document.createTextNode(element['duck_name']));
                let duckTaggedCell = duckRow.insertCell(1);
                duckTaggedCell.appendChild(document.createTextNode(yyyymmdd(new Date(element['tagged']))));
            });
            console.log(data);
        });
}

function handleDuckForm() {
    const duckForm = document.getElementById('duckCreator');
    duckForm.addEventListener('submit', function (ev) {
        ev.preventDefault();
        postData(duckForm.action, 'name=' + document.getElementById('name').value
            + '&tagged=' + document.getElementById('tagged').value)
            .then((response) => {
                if (response['error'] != null && response['message'] != null) {
                    document.getElementById('errors').innerHTML = response['message'].toString().replaceAll('\n', '<br>\n');
                    return;
                } else {
                    document.getElementById('errors').innerHTML = '';
                }
                loadDucks();
                document.getElementById('duckCreator').reset();
            })
    });
}

ready(loadDucks);
ready(handleDuckForm);
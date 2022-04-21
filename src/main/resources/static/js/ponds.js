import { postData, ready } from "./base.js";

function loadPonds() {
    const pondTable = document.getElementById("ponds");
    const previousRows = pondTable.querySelectorAll('tr');
    Array.prototype.forEach.call(previousRows, function(row) { row.remove(); });
    fetch('/api/ponds')
        .then(response => response.json())
        .then(data => {
            data.forEach(element => {
                let pondRow = pondTable.insertRow(-1);
                let pondNameCell = pondRow.insertCell(0);
                pondNameCell.appendChild(document.createTextNode(element['pond_name']));
                let pondLocationCell = pondRow.insertCell(1);
                pondLocationCell.appendChild(document.createTextNode(element['pond_location']));
            });
            console.log(data);
        });
}

function handlePondForm() {
    const pondForm = document.getElementById('pondCreator');
    pondForm.addEventListener('submit', function (ev) {
        ev.preventDefault();
        postData(pondForm.action, 'name=' + document.getElementById('name').value
            + '&location=' + document.getElementById('location').value)
            .then((response) => {
                if (response['error'] != null && response['message'] != null) {
                    document.getElementById('errors').innerHTML = response['message'].toString().replaceAll('\n', '<br>\n');
                    return;
                } else {
                    document.getElementById('errors').innerHTML = '';
                }
                loadPonds();
                document.getElementById('pondCreator').reset();
            })
    });
}

ready(loadPonds);
ready(handlePondForm);
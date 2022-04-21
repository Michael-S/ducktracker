import { postData, ready, yyyymmdd } from "./base.js";

function loadDucksAndPonds() {
    const duckSelect = document.getElementById('duck_id');
    fetch('/api/ducks')
        .then(response => response.json())
        .then(data => {
            data.forEach(duck => {
                duckSelect[duckSelect.options.length] = new Option(duck['duck_name'], duck['id']);
            });
        });
    const pondSelect = document.getElementById('pond_id');
    fetch('/api/ponds')
        .then(response => response.json())
        .then(data => {
            data.forEach(pond => {
                pondSelect[pondSelect.options.length] = new Option(pond['pond_name'], pond['id']);
            });
        });

}

function loadDuckTravels() {
    const duckTravelsTable = document.getElementById("ducktravels");
    const previousRows = duckTravelsTable.querySelectorAll('tr:not(:first-child)');
    Array.prototype.forEach.call(previousRows, function(row) { row.remove(); });
    fetch('/api/ducktravels')
        .then(response => response.json())
        .then(data => {
            data.forEach(element => {
                let duckTravelsRow = duckTravelsTable.insertRow(-1);
                let duckNameCell = duckTravelsRow.insertCell(0);
                duckNameCell.appendChild(document.createTextNode(element['duck_name']));
                let pondNameCell = duckTravelsRow.insertCell(1);
                pondNameCell.appendChild(document.createTextNode(element['pond_name']));
                let duckArrivalCell = duckTravelsRow.insertCell(2);
                duckArrivalCell.appendChild(document.createTextNode(yyyymmdd(new Date(element['arrival']))));
                let duckDepartureCell = duckTravelsRow.insertCell(3);
                let departure = element['departure'];
                if (departure != null /* I should probably check for undefined */) {
                    duckDepartureCell.appendChild(document.createTextNode(yyyymmdd(new Date(element['departure']))));
                }
            });
            console.log(data);
        });
}

function handleDuckTravelsForm() {
    const duckTravelForm = document.getElementById('duckTravelCreator');
    duckTravelForm.addEventListener('submit', function (ev) {
        ev.preventDefault();
        let formData = 'duck_id=' + document.getElementById('duck_id').value
            + '&pond_id=' + document.getElementById('pond_id').value +
            '&arrival=' + document.getElementById('arrival').value;
        let departure = document.getElementById('departure').value;
        if (departure != null) {
            formData = formData + '&departure=' + departure;
        }
        postData(duckTravelForm.action, formData)
            .then((response) => {
                if (response['error'] != null && response['message'] != null) {
                    document.getElementById('errors').innerHTML = response['message'].toString().replaceAll('\n', '<br>\n');
                    return;
                } else {
                    document.getElementById('errors').innerHTML = '';
                }
                loadDuckTravels();
                document.getElementById('duckTravelCreator').reset();
            })
    });
}

ready(loadDuckTravels);
ready(loadDucksAndPonds);
ready(handleDuckTravelsForm);

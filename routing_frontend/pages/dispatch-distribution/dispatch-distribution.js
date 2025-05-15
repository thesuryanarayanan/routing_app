var selectedRange = null;

var isFiltered = false;

async function fetchVehicles() {
  try {
    const response = await fetch("http://localhost:8070/vehicles");
    if (!response.ok) {
      throw new Error(await response.text());
    }

    const vehicles = await response.json();
    const vehicleSelect = document.getElementById("vehicle");

    setRange(vehicles[0]?.deliveryRange);

    vehicles.forEach((vehicle) => {
      const option = document.createElement("option");
      option.value = vehicle.id;
      option.textContent = deliveryRangeEnumToText(vehicle?.deliveryRange);

      option.label = `Araç Tipi: ${vehicleTypeEnumToText(
        vehicle.vehicleType
      )} | Plaka: ${
        vehicle.licensePlate
      } | Araç Dağıtım Aralığı: ${deliveryRangeEnumToText(
        vehicle?.deliveryRange
      )}`;

      vehicleSelect.appendChild(option);
    });
  } catch (error) {
    alert(`Araçlar listelenirken hata oluştu: ${error.message}`);
  }
}

document.getElementById("vehicle").addEventListener("change", async (event) => {
  const vehicleSelect = document.getElementById("vehicle");
  var range = vehicleSelect.options[vehicleSelect.selectedIndex].text;
  setRange(deliveryRangeTextToEnum(range));
  clearDispatches();
  fetchDispatches();
});

function setRange(range) {
  console.log(range);
  selectedRange = range;
}

function clearDispatches() {
  const dispatchSelect = document.getElementById("dispatch");
  while (dispatchSelect.options.length > 0) {
    dispatchSelect.remove(0);
  }
}

async function fetchDispatches() {
  try {
    const response = await fetch("http://localhost:8070/dispatches");
    if (!response.ok) {
      throw new Error(await response.text());
    }

    var dispatches = await response.json();
    const dispatchSelect = document.getElementById("dispatch");
    var filteredDispatches = [];

    dispatches.forEach((dispatch) => {
      console.log(dispatch.deliveryRange + " - " + selectedRange);

      if (dispatch.deliveryRange === selectedRange) {
        filteredDispatches.push(dispatch);
      } else if (
        dispatch.deliveryRange === "MORNING" &&
        selectedRange === "MORNING"
      ) {
        filteredDispatches.push(dispatch);
      } else if (
        dispatch.deliveryRange === "MORNING" &&
        selectedRange === "MIDMORNING"
      ) {
        filteredDispatches.push(dispatch);
      } else if (
        dispatch.deliveryRange === "MIDMORNING" &&
        selectedRange === "MORNING"
      ) {
        filteredDispatches.push(dispatch);
      } else if (
        dispatch.deliveryRange === "MIDMORNING" &&
        selectedRange === "MIDMORNING"
      ) {
        filteredDispatches.push(dispatch);
      } else if (
        dispatch.deliveryRange === "AFTERNOON" &&
        selectedRange === "AFTERNOON"
      ) {
        filteredDispatches.push(dispatch);
      } else if (
        dispatch.deliveryRange === "AFTERNOON" &&
        selectedRange === "EVENING"
      ) {
        filteredDispatches.push(dispatch);
      } else if (
        dispatch.deliveryRange === "EVENING" &&
        selectedRange === "AFTERNOON"
      ) {
        filteredDispatches.push(dispatch);
      } else if (
        dispatch.deliveryRange === "EVENING" &&
        selectedRange === "EVENING"
      ) {
        filteredDispatches.push(dispatch);
      }
    });

    dispatches = [];
    filteredDispatches.forEach((dispatch) => {
      console.log(dispatch.deliveryRange);
      if (isFiltered) {
        if (isExistPreferedDeliveryTime(dispatch)) {
          dispatches.push(dispatch);
        }
      }
    });

    (isFiltered ? dispatches : filteredDispatches).forEach((dispatch) => {
      const option = document.createElement("option");
      option.value = dispatch.id;
      option.textContent = ` Müşteri: ${dispatch.customer.firstName} ${
        dispatch.customer.lastName
      } | Saat Aralığı: ${
        dispatch.preferFirstDeliveryTime && dispatch.preferLastDeliveryTime
          ? `${new Date(
              dispatch.preferFirstDeliveryTime * 1000
            ).toLocaleTimeString()} ile ${new Date(
              dispatch.preferLastDeliveryTime * 1000
            ).toLocaleTimeString()} arası.`
          : "Bulunmamakta."
      }  | Teslimat Aralığı: ${deliveryRangeEnumToText(
        dispatch.deliveryRange
      )}`;

      dispatchSelect.appendChild(option);
    });
  } catch (error) {
    alert(`Zimmetler listelenirken hata oluştu: ${error.message}`);
  }
}

document
  .getElementById("cargoForm")
  .addEventListener("submit", async (event) => {
    event.preventDefault();

    const selectDispatch = document.getElementById("dispatch");

    const selectedDispatches = Array.from(selectDispatch.selectedOptions).map(
      (option) => ({
        id: parseInt(option.value),
      })
    );

    const data = {
      vehicle: {
        id: parseInt(document.getElementById("vehicle").value),
      },
      dispatch: selectedDispatches,
    };

    try {
      const response = await fetch("http://localhost:8070/dispatch-vehicles", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });
      if (!response.ok) {
        throw new Error(await response.text());
      }

      alert("Dağıtım oluşturma başarılı!");
      document.getElementById("cargoForm").reset();
    } catch (error) {
      alert(`Dağıtım oluşturulurken hata oluştu: ${error.message}`);
    }
  });

function filterByTime() {
  var checkBox = document.getElementById("myCheck");
  isFiltered = checkBox.checked;
  clearDispatches();
  fetchDispatches();
  console.log("filterByTime" + isFiltered);
}

try {
  fetchVehicles().then(() => {
    fetchDispatches();
  });
} catch (error) {
  alert(`Sayfa yüklenirken hata oluştu: ${error.message}`);
}

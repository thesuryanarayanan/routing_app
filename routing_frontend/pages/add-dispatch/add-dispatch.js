let selectedLatitude = null;
let selectedLongitude = null;

let selectedDeliveryRange = "MORNING";

let preferFirstDeliveryTime = null;
let preferLastDeliveryTime = null;

var selectedBranch = JSON.parse(sessionStorage.getItem("selectedBranch"));

document.addEventListener("DOMContentLoaded", () => {
  const map = L.map("map").setView([41.098893, 28.893887], 12);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: "© OpenStreetMap contributors",
  }).addTo(map);

  const marker = L.marker([41.098893, 28.893887], { draggable: true }).addTo(
    map
  );

  L.rectangle(
    [
      [
        selectedBranch.boundingBoxLatitude1,
        selectedBranch.boundingBoxLongitude1,
      ],
      [
        selectedBranch.boundingBoxLatitude2,
        selectedBranch.boundingBoxLongitude2,
      ],
    ],
    {
      color: "#ff7800",
      weight: 2,
      fillOpacity: 0.3,
    }
  ).addTo(map);

  L.marker([selectedBranch?.latitude, selectedBranch?.longitude], {
    icon: L.icon({
      iconUrl: "../../img/branch.png",
      iconSize: [50, 50],
    }),
  })
    .addTo(map)
    .bindTooltip("", {
      permanent: true,
      opacity: 0.0,
      direction: "bottom",
      className: "labelstyle",
    });

  marker.on("dragend", (event) => {
    const { lat, lng } = event.target.getLatLng();
    selectedLatitude = lat;
    selectedLongitude = lng;

    document.getElementById("latitude").textContent = lat.toFixed(6);
    document.getElementById("longitude").textContent = lng.toFixed(6);
    try {
      fetch(
        `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`
      ).then(async (response) => {
        if (!response.ok) {
          throw new Error(await response.text());
        }
        response.json().then((address) => {
          document.getElementById("address").textContent = address.display_name;
        });
      });
    } catch (error) {
      alert(`Adres bilgisi alınırken hata oluştu: ${error.message}`);
    }
  });

  fetchCustomers();
});

async function fetchCustomers() {
  try {
    const response = await fetch("http://localhost:8070/customers");
    if (!response.ok) {
      throw new Error(await response.text());
    }
    const customers = await response.json();
    const customerSelect = document.getElementById("customer");

    customers.forEach((customer) => {
      const option = document.createElement("option");
      option.value = customer.id;
      option.textContent = `${customer.firstName} ${customer.lastName}`;
      customerSelect.appendChild(option);
    });
  } catch (error) {
    alert(`Müşteriler listenirken hata oluştu: ${error.message}`);
  }
}

document
  .getElementById("deliveryRange")
  .addEventListener("change", async (event) => {
    selectedDeliveryRange = event.target.value;
    console.log(selectedDeliveryRange);
  });

document
  .getElementById("firstDate")
  .addEventListener("change", async (event) => {
    checkDate();
  });

document
  .getElementById("lastDate")
  .addEventListener("change", async (event) => {
    checkDate();
  });

function checkDate() {
  var firstDate = document.getElementById("firstDate").value;
  var lastDate = document.getElementById("lastDate").value;

  if (firstDate != "" && lastDate != "") {
    if (firstDate > lastDate) {
      alert("İlk tarih son tarihten büyük olamaz!");
      document.getElementById("firstDate").value = "";
      document.getElementById("lastDate").value = "";
    } else {
      if (lastDate.slice(0, 2) - firstDate.slice(0, 2) < 1) {
        alert("Teslimat aralığı en az 1 saat olmalıdır!");
        document.getElementById("firstDate").value = "";
        document.getElementById("lastDate").value = "";
      } else {
        var currentFirstDate = new Date();
        currentFirstDate.setHours(
          firstDate.slice(0, 2),
          firstDate.slice(3, 5),
          0,
          0
        );
        var newFirstDate = currentFirstDate;
        var currentLastDate = new Date();
        currentLastDate.setHours(
          lastDate.slice(0, 2),
          lastDate.slice(3, 5),
          0,
          0
        );
        var newLastDate = currentLastDate;

        preferFirstDeliveryTime = newFirstDate.getTime() / 1000;
        preferLastDeliveryTime = newLastDate.getTime() / 1000;

        if (firstDate.slice(0, 2) >= 7 && lastDate.slice(0, 2) < 10) {
          selectedDeliveryRange = "MORNING";
          document.getElementById("deliveryRange").value = "MORNING";
        } else if (firstDate.slice(0, 2) >= 10 && lastDate.slice(0, 2) < 13) {
          selectedDeliveryRange = "MIDMORNING";
          document.getElementById("deliveryRange").value = "MIDMORNING";
        } else if (firstDate.slice(0, 2) >= 13 && lastDate.slice(0, 2) < 16) {
          selectedDeliveryRange = "AFTERNOON";
          document.getElementById("deliveryRange").value = "AFTERNOON";
        } else if (firstDate.slice(0, 2) >= 16 && lastDate.slice(0, 2) < 19) {
          selectedDeliveryRange = "EVENING";
          document.getElementById("deliveryRange").value = "EVENING";
        }
      }
    }
  }
}

document
  .getElementById("cargoForm")
  .addEventListener("submit", async (event) => {
    event.preventDefault();

    if (!selectedLatitude || !selectedLongitude) {
      alert("Lütfen haritadan konum seçiniz.");
      return;
    }

    const data = {
      dispatchType: document.getElementById("dispatchType").value,
      weight: parseFloat(document.getElementById("weight").value),
      customer: {
        id: parseInt(document.getElementById("customer").value),
      },
      receiverLatitude: selectedLatitude,
      receiverLongitude: selectedLongitude,
      deliveryRange: selectedDeliveryRange,
      receiverAddress: document.getElementById("address").textContent,
      preferFirstDeliveryTime: preferFirstDeliveryTime,
      preferLastDeliveryTime: preferLastDeliveryTime,
    };

    if (
      isWithinBoundingBox(
        selectedLatitude,
        selectedLongitude,
        selectedBranch.boundingBoxLatitude1,
        selectedBranch.boundingBoxLongitude1,
        selectedBranch.boundingBoxLatitude2,
        selectedBranch.boundingBoxLongitude2
      ) === false
    ) {
      alert("Teslimat adresi şube sınırları içinde değil!");
      return;
    }

    try {
      const response = await fetch("http://localhost:8060/dispatches", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        throw new Error(await response.text());
      }

      alert("Zimmet başarılı bir şekilde eklendi!");

      document.getElementById("cargoForm").reset();
      document.getElementById("deliveryRange").value = "MORNING";
      selectedDeliveryRange = "MORNING";
      document.getElementById("firstDate").value = "";
      document.getElementById("lastDate").value = "";
      preferFirstDeliveryTime = null;
      preferLastDeliveryTime = null;
      document.getElementById("latitude").textContent = "Not Selected";
      document.getElementById("longitude").textContent = "Not Selected";
    } catch (error) {
      alert(`Zimmet eklenirken hata oluştu: ${error.message}`);
    }
  });

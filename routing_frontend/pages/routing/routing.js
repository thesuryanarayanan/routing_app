let map;

var selectedBranch = JSON.parse(sessionStorage.getItem("selectedBranch"));

let outboundPolyline = null;
let returnPolyline = null;

function showPosition(position) {
  map = L.map("map").setView(
    [position.coords.latitude, position.coords.longitude],
    13
  );

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution:
      '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
  }).addTo(map);

  drawBoundingBox();
}

function drawBoundingBox() {
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
}

async function fetchDistributions() {
  try {
    const response = await fetch("http://localhost:8070/dispatch-vehicles");
    if (!response.ok) {
      throw new Error(await response.text());
    }
    const distributions = await response.json();
    const distributionSelect = document.getElementById("distribution");

    distributions.forEach((distribution) => {
      const option = document.createElement("option");
      option.value = distribution.id;
      option.textContent = `Şube Adı: ${
        distribution.vehicle.branch.name
      } | Araç Tipi: ${vehicleTypeEnumToText(
        distribution.vehicle.vehicleType
      )} | Araç Plakası:  ${
        distribution.vehicle.licensePlate
      } | Rota Saati:  ${new Date(
        distribution.routeDate * 1000
      ).toLocaleTimeString(
        "tr-TR"
      )} | Araç Dağıtım Aralığı: ${deliveryRangeEnumToText(
        distribution.vehicle.deliveryRange
      )}`;
      distributionSelect.appendChild(option);
    });
  } catch (error) {
    alert(`Dağıtımlar listelenirken hata oluştu: ${error.message}`);
  }
}

async function handleFormSubmit(event) {
  event.preventDefault();
  const selectedDistributionId = document.getElementById("distribution").value;
  clearMap();
  try {
    const response = await fetch(
      `http://localhost:8070/dispatch-vehicles/route/${selectedDistributionId}`
    );
    if (!response.ok) {
      throw new Error(await response.text());
    }
    const res = await response.json();
    const vehicle = res.vehicle;
    const dispatches = res.dispatches;
    const routeGeometry = res.geometry;
    const totalDurationMinutes = res.totalDurationMinutes;
    const totalDurationFormatted = res.totalDurationFormatted;
    const route = res.routes[0];
    const services = route.services;
    const totalDistance = res.totalDistance / 1000;

    services.forEach((service, index) => {
      const dispatch = dispatches.find(
        (d) => d.receiverLatitude == service.location.latitude
      );
      const customer = dispatch?.customer;
      var arrivalTime = service.arrivalTime;
      var departureTime = service.endTime;

      L.marker([service.location.latitude, service.location.longitude], {
        icon: L.icon({
          iconUrl: isExistPreferedDeliveryTime(dispatch)
            ? "../../img/marker-green.png"
            : "../../img/marker.png",
          iconSize: [50, 50],
        }),
      })
        .addTo(map)
        .bindTooltip(`${index + 1}`, {
          permanent: true,
          opacity: dispatch == null ? 0.0 : 1.0,
          direction: "bottom",
          className: "labelstyle",
        })
        .bindPopup(
          `${index + 1}. Durak | ${customer?.firstName} ${
            customer?.lastName
          } | ${deliveryRangeEnumToText(
            dispatch?.deliveryRange
          )} | Tercih Edilen Saatler:</strong> ${
            dispatch.preferFirstDeliveryTime && dispatch.preferLastDeliveryTime
              ? `${new Date(
                  dispatch.preferFirstDeliveryTime * 1000
                ).toLocaleTimeString()} ile ${new Date(
                  dispatch.preferLastDeliveryTime * 1000
                ).toLocaleTimeString()} arası. <br>`
              : "Bulunmamakta. <br>"
          }| Tahmini Varis Zamani: ${arrivalTime}
                | Tahmini Ayrilis Zamani: ${departureTime}`
        )
        .openPopup();
    });

    L.marker([vehicle?.branch?.latitude, vehicle?.branch?.longitude], {
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
      })
      .bindPopup(
        `${
          vehicle?.branch?.name
        } | Araç Dağıtım Aralığı: ${deliveryRangeEnumToText(
          vehicle?.deliveryRange
        )} | Araç Dağıtım Saat Aralığı: ${
          getDeliveryRangeHours(vehicle?.deliveryRange)[0] +
          ":00" +
          " ile " +
          getDeliveryRangeHours(vehicle?.deliveryRange)[1] +
          ":00"
        } | Tahmini Araç Dönüş Saati: ${createDateWithTime(
          getDeliveryRangeHours(vehicle?.deliveryRange)[0],
          totalDurationMinutes
        )}`
      )
      .openPopup();

    outboundPolyline = L.polyline(
      routeGeometry.outbound.map((coord) => [coord[1], coord[0]]),
      {
        color: "blue",
        weight: 6,
      }
    ).addTo(map);

    returnPolyline = L.polyline(
      routeGeometry.return.map((coord) => [coord[1], coord[0]]),
      {
        color: "purple",
        weight: 6,
      }
    ).addTo(map);

    const bounds = L.featureGroup([
      outboundPolyline,
      returnPolyline,
    ]).getBounds();
    map.fitBounds(bounds);
    alert(
      `Toplam Mesafe: ${totalDistance.toFixed(
        1
      )} km \nSüre: ${totalDurationFormatted}`
    );
  } catch (error) {
    alert(`Rota oluşturulurken hata oluştu: ${error.message}`);
  }
}

function clearMap() {
  map.eachLayer(function (layer) {
    if (layer instanceof L.Marker || layer instanceof L.Polyline) {
      map.removeLayer(layer);
    }
  });
  outboundPolyline = null;
  returnPolyline = null;
  drawBoundingBox();
}

document
  .getElementById("toggleOutbound")
  .addEventListener("click", function () {
    if (outboundPolyline) {
      if (map.hasLayer(outboundPolyline)) {
        map.removeLayer(outboundPolyline);
      } else {
        outboundPolyline.addTo(map);
      }
    }
  });

document.getElementById("toggleReturn").addEventListener("click", function () {
  if (returnPolyline) {
    if (map.hasLayer(returnPolyline)) {
      map.removeLayer(returnPolyline);
    } else {
      returnPolyline.addTo(map);
    }
  }
});

document
  .getElementById("cargoForm")
  .addEventListener("submit", handleFormSubmit);

try {
  showPosition({
    coords: {
      latitude: selectedBranch.latitude,
      longitude: selectedBranch.longitude,
    },
  });
  fetchDistributions();
} catch (error) {
  alert("Hata: " + error);
}

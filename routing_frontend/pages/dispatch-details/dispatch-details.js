function getQueryParams() {
  const params = new URLSearchParams(window.location.search);
  return {
    id: params.get("id"),
  };
}

async function fetchDispatchById(id) {
  try {
    const response = await fetch(`http://localhost:8070/dispatches/${id}`);
    if (!response.ok) {
      throw new Error(await response.text());
    }
    const dispatch = await response.json();
    if (!dispatch) throw new Error("Dispatch not found!");
    return dispatch;
  } catch (error) {
    alert(`Zimmet listelenirken hata oluştu: ${error.message}`);
  }
}

async function initialize() {
  const { id } = getQueryParams();
  const dispatch = await fetchDispatchById(id);

  if (dispatch) {
    document.getElementById(
      "dispatch-title"
    ).textContent = `Zimmet #${dispatch.id}`;
    document.getElementById("dispatch-details").innerHTML = `
        <strong>Zimmet Tipi:</strong> ${dispatchTypeEnumToText(
          dispatch.dispatchType
        )} <br>
        <strong>Ağırlık:</strong> ${dispatch.weight} kg <br>
        <strong>Tercih Edilen Saatler:</strong> ${
          dispatch.preferFirstDeliveryTime && dispatch.preferLastDeliveryTime
            ? `${new Date(
                dispatch.preferFirstDeliveryTime * 1000
              ).toLocaleTimeString()} ile ${new Date(
                dispatch.preferLastDeliveryTime * 1000
              ).toLocaleTimeString()} arası. <br>`
            : "Bulunmamakta. <br>"
        }
        <strong>Teslimat Aralığı:</strong> ${deliveryRangeEnumToText(
          dispatch.deliveryRange
        )} <br>
        <strong>Müşteri:</strong> ${dispatch.customer.firstName} ${
      dispatch.customer.lastName
    } (${dispatch.customer.phone}) <br>
        <strong>Koordinatlar:</strong> (${dispatch.receiverLatitude}, ${
      dispatch.receiverLongitude
    }) <br>
        <strong>Adres:</strong> ${dispatch.receiverAddress}
      `;
    const map = L.map("map").setView(
      [dispatch.receiverLatitude, dispatch.receiverLongitude],
      13
    );
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 19,
    }).addTo(map);
    const marker = L.marker([
      dispatch.receiverLatitude,
      dispatch.receiverLongitude,
    ])
      .addTo(map, {
        icon: L.icon({
          iconUrl: "../../img/marker.png",
          iconSize: [50, 50],
        }),
      })
      .bindPopup(
        `<b>Detay</b>: ${dispatch.customer.firstName} ${dispatch.customer.lastName} <br> <b>Koordinatlar</b>: ${dispatch.receiverLatitude}, ${dispatch.receiverLongitude}`
      )
      .openPopup();
  } else {
    document.getElementById("dispatch-details").textContent =
      "Zimmet Bulunamadı.";
  }
}

initialize();

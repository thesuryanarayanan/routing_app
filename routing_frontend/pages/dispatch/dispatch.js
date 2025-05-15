async function fetchDispatches() {
  try {
    const response = await fetch("http://localhost:8070/dispatches");

    if (!response.ok) {
      throw new Error(await response.text());
    }

    const dispatches = await response.json();

    displayDispatches(dispatches);
  } catch (error) {
    alert(`Zimmetler listenirken hata oluştu: ${error.message}`);
  }
}

function displayDispatches(dispatches) {
  const container = document.getElementById("dispatch-container");

  container.innerHTML = "";

  dispatches.forEach((dispatch) => {
    const card = document.createElement("div");
    card.className = "col-sm-6 mb-3";

    card.innerHTML = `
            <div class="card">
              <div class="card-body">
                <h5 class="card-title">Zimmet #${dispatch.id}</h5>
                <p class="card-text">
                  <strong>Müşteri:</strong> ${dispatch.customer.firstName} ${
      dispatch.customer.lastName
    } (${dispatch.customer.phone}) <br>
                  <strong>Tercih Edilen Saatler:</strong> ${
                    dispatch.preferFirstDeliveryTime &&
                    dispatch.preferLastDeliveryTime
                      ? `${new Date(
                          dispatch.preferFirstDeliveryTime * 1000
                        ).toLocaleTimeString()} ile ${new Date(
                          dispatch.preferLastDeliveryTime * 1000
                        ).toLocaleTimeString()} arası. <br>`
                      : "Bulunmamakta. <br>"
                  }
                  <strong>Koordinat:</strong> (${dispatch.receiverLatitude}, ${
      dispatch.receiverLongitude
    }) <br>
                   <strong>Adres:</strong> ${dispatch.receiverAddress}<br>
                   
                </p>
                <a href="../dispatch-details/dispatch-details.html?id=${
                  dispatch.id
                }" class="btn btn-primary">Detay</a>
              </div>
            </div>
          `;

    container.appendChild(card);
  });
}

fetchDispatches();

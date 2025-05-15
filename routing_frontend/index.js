async function fetchBranches() {
  try {
    const response = await fetch("http://localhost:8070/branches");

    if (!response.ok) {
      throw new Error(await response.text());
    }

    const branches = await response.json();

    displayBranches(branches);
  } catch (error) {
    alert(`Şubeler listenirken hata oluştu: ${error.message}`);
  }
}

function displayBranches(branches) {
  const container = document.getElementById("branch-container");

  container.innerHTML = "";

  branches.forEach((branch, index) => {
    const card = document.createElement("div");
    card.className = "col-sm-6 mb-3";

    const cardTitle = document.createElement("h5");
    cardTitle.className = "card-title";

    cardTitle.textContent = `Şube Ad: ${branch.name}`;

    cardTitle.addEventListener("click", function () {
      handleBranchClick(branch);
    });

    const cardBody = document.createElement("div");
    cardBody.className = "card-body";
    cardBody.appendChild(cardTitle);

    const cardDiv = document.createElement("div");
    cardDiv.className = "card";
    cardDiv.appendChild(cardBody);

    card.appendChild(cardDiv);

    container.appendChild(card);
  });
}

function handleBranchClick(branch) {
  sessionStorage.setItem("selectedBranch", JSON.stringify(branch));

  window.location.href = `pages/home/home.html`;
}

fetchBranches();

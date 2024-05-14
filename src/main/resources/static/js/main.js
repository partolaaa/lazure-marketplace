function prepareCategoryToolTip(category) {
    const tooltip = document.createElement("div");
    tooltip.className = "tooltip";
    const infoIcon = document.createElement('img');
    infoIcon.src = 'img/info.png';
    infoIcon.alt = 'Info';
    infoIcon.className = 'info-icon';

    const tooltipText = document.createElement("span");

    tooltipText.classList.add("tooltiptext");
    tooltipText.classList.add("tooltip-left");
    tooltipText.innerText = category.description;

    tooltip.appendChild(infoIcon);
    tooltip.appendChild(tooltipText);

    return tooltip;
}
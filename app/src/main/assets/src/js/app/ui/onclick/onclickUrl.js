export function addUrlOnclick(page) {
  return () => {
    let newItem = ons.createElement('<ons-list-item data-unsaved="true" data-removed="false" modifier="nodivider">');
    let urlInput = ons.createElement('<ons-input modifier="underbar" placeholder="Url" float>');

    let rightContent = ons.createElement('<div class="right">');
    let removeIcon = ons.createElement('<ons-icon class="removeIcon" icon="md-minus-circle">');
    removeIcon.style.color = 'red';
    removeIcon.onclick = () => {
      newItem.remove();
    };

    rightContent.append(removeIcon);

    newItem.append(urlInput);
    newItem.append(rightContent);
    page.querySelector('#url-items').append(newItem);
  };
}


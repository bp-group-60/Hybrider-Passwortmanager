export function createUrlItem(url) {
  let listItem = ons
    .createElement('<ons-list-item data-unsaved="false" data-removed="false" modifier="nodivider">');
  listItem.innerText = url;
  let rightContent = ons.createElement('<div class="right">');
  let removeIcon = ons
    .createElement('<ons-icon class="removeIcon" icon="md-minus-circle">');
  removeIcon.style.display = 'none';
  removeIcon.style.color = 'red';
  removeIcon.onclick = () => {
    listItem.style.display = 'none';
    listItem.setAttribute('data-unsaved', 'true');
    listItem.setAttribute('data-removed', 'true');
  }

  rightContent.append(removeIcon);
  listItem.append(rightContent);
  return listItem;
}

export function getAddedUrls(page) {
  let a = [];
  page.querySelector('#url-items').childNodes.forEach(item => {
    if (item.getAttribute('data-unsaved') === 'true' &&
      item.getAttribute('data-removed') === 'false') {
      a.push(item.querySelector('ons-input').value);
    }
  });
  return a.filter(elm => elm !== '');
}

export function getRemovedUrls(page) {
  let a = [];
  page.querySelector('#url-items').childNodes.forEach(item => {
    if (item.getAttribute('data-unsaved') === 'true' &&
      item.getAttribute('data-removed') === 'true') {
      a.push(item.innerText);
    }
  });
  return a;
}
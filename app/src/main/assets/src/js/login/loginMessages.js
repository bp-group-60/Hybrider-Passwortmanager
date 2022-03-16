export function setFormMessage(formElement, type, message) {
  const messageElement = formElement.querySelector('.message-form');

  messageElement.textContent = message;
  messageElement.classList.remove('message-success-form', 'error-message-form');
  messageElement.classList.add(`form__message--${type}`);
}

export function setInputError(inputElement, message) {
  inputElement.classList.add('input-error-form');
  inputElement.parentElement.querySelector('.input-error-message-form').textContent = message;
}

export function clearInputError(inputElement) {
  inputElement.classList.remove('input-error-form');
  inputElement.parentElement.querySelector('.input-error-message-form').textContent = '';
}

function clearAllErrorMessages(formElement) {
  formElement.querySelector('.error-message-form').textContent = '';

  formElement.querySelectorAll('.input-form').forEach(inputElement => {
    inputElement.value = '';
    clearInputError(inputElement);
  });
}
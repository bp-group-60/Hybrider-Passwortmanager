export function setFormMessage(formElement, type, message) {
  const messageElement = formElement.querySelector('.form-message');

  messageElement.textContent = message;
  messageElement.classList.remove('form-message-success', 'form-message-error');
  messageElement.classList.add(`form__message--${type}`);
}

export function setInputError(inputElement, message) {
  inputElement.classList.add('form-input-error');
  inputElement.parentElement.querySelector('.form-input-message-error').textContent = message;
}

export function clearInputError(inputElement) {
  inputElement.classList.remove('form-input-error');
  inputElement.parentElement.querySelector('.form-input-message-error').textContent = '';
}

function clearAllErrorMessages(formElement) {
  formElement.querySelector('.form-message-error').textContent = '';

  formElement.querySelectorAll('.form-input').forEach(inputElement => {
    inputElement.value = '';
    clearInputError(inputElement);
  });
}
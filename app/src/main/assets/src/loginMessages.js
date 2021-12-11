export function setFormMessage(formElement, type, message) {
    const messageElement = formElement.querySelector(".form__message")

    messageElement.textContent = message
    messageElement.classList.remove("form__message--success", "form__message--error")
    messageElement.classList.add(`form__message--${type}`)
}

export function setInputError(inputElement, message) {
    inputElement.classList.add("form__input--error")
    inputElement.parentElement.querySelector(".form__input-error-message").textContent = message
}

export function clearInputError(inputElement) {
    inputElement.classList.remove("form__input--error")
    inputElement.parentElement.querySelector(".form__input-error-message").textContent = ""
}

function clearAllErrorMessages(formElement) {
    formElement.querySelector(".form__message--error").textContent = ""

    formElement.querySelectorAll(".form__input").forEach(inputElement => {
        inputElement.value = ""
        clearInputError(inputElement)
    })

}
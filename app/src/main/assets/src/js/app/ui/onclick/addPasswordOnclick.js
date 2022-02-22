import {getSessionUser} from '../../sessionHandler.js';
import {createPassword} from '../../extern/database/passwordOperations.js';
import {saveUrlList} from '../../extern/database/websiteOperations.js';
import {getAddedUrls} from '../urlHandler.js';

export function commitButtonOnclick(page) {
  return () => {

    console.log('TODO: validate data');

    let name = document.getElementById('name').value;
    let loginName = document.getElementById('username').value;
    let password = document.getElementById('password').value;
    let successful = createPassword(getSessionUser(), name, loginName, password);

    if (successful) {
      saveUrlList(getSessionUser(), name, getAddedUrls(page));
      document.querySelector('#myNavigator').popPage();
      ons.notification.toast('Passwort hinzugefügt!', {timeout: 3000});
    } else {
      ons.notification.toast('Fehler beim speichern!', {timeout: 3000});
    }
  };
}

export function generateRandomPasswordOnclick(page, len) {
  return () => {
    var GeneratePasswordmodule = Module.cwrap("GenerateRandomPasswortbyC06", null, ["number","number"]);

    var output_array = new Int32Array(Module.HEAP32.buffer, 0, len);
    var bytes_per_element = output_array.BYTES_PER_ELEMENT;
    var output_ptr = Module._malloc(len * bytes_per_element);
    Module.HEAP32.set(output_array, output_ptr / bytes_per_element);
    GeneratePasswordmodule(output_ptr, len);
    output_array = new Int32Array(Module.HEAP32.buffer, output_ptr, len);
    var generatedPassword = String.fromCharCode.apply(null, output_array);

    Module._free(output_ptr);

    page.querySelector("#password").value = generatedPassword;
    //page.getElementById('password').value = generatedRandomPassword;
  }
}

export function generateRandomUsernameOnclick(page, len) {
  return () => {
    var GenerateUsernameModule = Module.cwrap("GenerateRandomPasswortbyC06", null, ["number","number"]);

    var output_array = new Int32Array(Module.HEAP32.buffer, 0, len);
    var bytes_per_element = output_array.BYTES_PER_ELEMENT;
    var output_ptr = Module._malloc(len * bytes_per_element);
    Module.HEAP32.set(output_array, output_ptr / bytes_per_element);
    GenerateUsernameModule(output_ptr, len);
    output_array = new Int32Array(Module.HEAP32.buffer, output_ptr, len);
    var generatedUsername = String.fromCharCode.apply(null, output_array);

    Module._free(output_ptr);

    page.querySelector("#username").value = generatedUsername;
    //page.getElementById('password').value = generatedRandomPassword;
    }
}

export function showPasswordOnclick(page) {
  return () => {
    if (page.querySelector('#passwordCheckbox').checked) {
      page.querySelector('#password').children[0].type = 'text';
    } else {
      page.querySelector('#password').children[0].type = 'password';
    }
  };
}
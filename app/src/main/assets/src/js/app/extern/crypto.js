export function generateKey(plainPassword, salt) {
  return Java_InterfaceCrypto.generateKey(plainPassword, salt);
}

export function hashPassword(userPassword, salt) {
  return Java_InterfaceCrypto.hashPassword(userPassword, salt);
}
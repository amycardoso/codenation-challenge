# Codenation Challenge: Julius Caesar Cryptography 👩🏽‍💻

The solution makes an HTTP request to the url below:
```sh
https://api.codenation.dev/v1/challenge/dev-ps/generate-data?token=YOUR_TOKEN
```
Gets the data generated according to the format below:
```sh
{
	"numero_casas": 10,
	"token":"token_do_usuario",
	"cifrado": "texto criptografado",
	"decifrado": "aqui vai o texto decifrado",
	"resumo_criptografico": "aqui vai o resumo"
}
```
It decrypts the encrypted phrase and then generates a cryptographic summary of the deciphered phrase using the sha1 algorithm.
Then submit a file with the solution via POST to the API:
```sh
https://api.codenation.dev/v1/challenge/dev-ps/submit-solution?token=YOUR_TOKEN
```
To use the code, add your token to the application.properties.

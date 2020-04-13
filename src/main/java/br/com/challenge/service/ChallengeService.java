package br.com.challenge.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.challenge.helper.ResponseJson;
import br.com.challenge.helper.Score;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static jdk.nashorn.internal.objects.NativeString.toLowerCase;

@Service
public class ChallengeService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${codenation.token}")
	private String token;

	private OkHttpClient httpClient;
	private Gson gson;
	private ResponseJson responseJson;

	@PostConstruct
	public void init() {
		this.httpClient = new OkHttpClient();
		this.gson = new Gson();
	}

	public ResponseEntity<ResponseJson> generateData() {

		Request request = new Request.Builder()
				.url("https://api.codenation.dev/v1/challenge/dev-ps/generate-data?token=" + token)
				.get()
				.build();

		try {
			Response response = httpClient.newCall(request).execute();
			this.responseJson = gson.fromJson(response.body().string(), ResponseJson.class);
			return ResponseEntity.ok().body(this.responseJson);
		} catch (Exception e) {
			logger.warn("Could not generate data", e);
			return ResponseEntity.badRequest().body(null);
		}

	}

	public String decrypt(String cifrado, int numero_casas) {
		cifrado = toLowerCase(cifrado);
		StringBuilder decrypted = new StringBuilder();
		int aux = 0;

		for (int x = 0; x < cifrado.length(); x++) {
			if (cifrado.charAt(x) >= 97 && cifrado.charAt(x) <= 122) {
				aux = (int) cifrado.charAt(x) - numero_casas;
				if (aux < 97) {
					aux = (123 - 97 % aux);
				}
			} else {
				aux = (int) cifrado.charAt(x);
			}
			decrypted.append((char) aux);
		}
		logger.info("Decrypted phrase", decrypted);
		return decrypted.toString();
	}

	public ResponseEntity<Score> submitSolution() {

		if (this.generateData().hasBody()) {
			
			this.responseJson.setDecifrado(this.decrypt(this.responseJson.getCifrado(), this.responseJson.getNumero_casas()));
			
			try {
				this.responseJson.setResumo_criptografico(this.decryptedToSha1(responseJson.getDecifrado()));
				FileWriter file = new FileWriter("answer.json");
				file.write(gson.toJson(responseJson));
				file.close();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("answer", "answer.json",
							RequestBody.create(MediaType.parse("application/octet-stream"),
							 new File("answer.json")))
					.build();

			Request request = new Request.Builder()
					.url("https://api.codenation.dev/v1/challenge/dev-ps/submit-solution?token=" + token)
					.post(body)
					.build();

			try {
				Response response = httpClient.newCall(request).execute();
				Score score = gson.fromJson(response.body().string(), Score.class);
				return ResponseEntity.ok().body(score);
			} catch (Exception e) {
				logger.warn("Could not submit solution", e);
				return ResponseEntity.badRequest().body(null);
			}

		} else {
			logger.warn("Could not submit solution");
			return ResponseEntity.badRequest().body(null);
		}
	}

	public String decryptedToSha1(String decrypted) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		sha1.update(decrypted.getBytes());
		byte[] digest = sha1.digest();

		return String.format("%1$040x", new BigInteger(1, digest));
	}

}
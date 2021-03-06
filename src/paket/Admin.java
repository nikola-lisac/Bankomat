package paket;

import java.util.ArrayList;
import java.util.Scanner;

import com.sun.javafx.fxml.ParseTraceElement;

public class Admin extends User {
	private static final String adminName = "nikola";
	private static final String adminPassword = "1234";

	static String getAdminName() {
		return adminName;
	}

	static String getAdminPassword() {
		return adminPassword;
	}

	/*
	 * Meni dostupan adminu. Admin unosi broj, u zavisnosti od njega poziva se
	 * metoda unutar switch-a
	 */
	public static void adminMenu() {

		System.out.println("\n******* Dobrodosli *******\n");
		System.out.println("Izaberite zeljenu opciju upisivanjem broja");
		System.out.println("[1] Unos novog korisnika");
		System.out.println("[2] Brisanje postojeceg korisnika");
		System.out.println("[3] Provjera stanja bankomata");
		System.out.println("[4] Unos novcanica");
		System.out.println("[5] Gasenje bankomata");
		System.out.println("[0] Log out");

		Scanner adminInput = new Scanner(System.in);
		int adminChoice = 0;// promjenljiva u kojoj se smjesta vrijednost koja
							// oznacava izabranu opciju admina
		/*
		 * ukoliko admin unese bilo koju vrijednost koja nije cijeli broj
		 * izbacuje poruku da je izvrsio pogresan unos i ponovo mu prikazuje
		 * meni
		 */
		try {
			adminChoice = adminInput.nextInt();
		} catch (Exception e) {
			System.out.println("--------------------------------------");
			System.out.println("\tPogresan unos!");
			System.out.println("--------------------------------------");
			adminMenu();
		}
		switch (adminChoice) {
		case 0: { // log out admina, program se vraca na pocetni login ekran
			System.out.println("\nHvala na posjeti, prijatan dan.\n");
			ATM.loginMenu();
			break;
		}
		case 1: {
			makeNewUser();// pravljenje novog korisnika i smjestanje u userList
			FileInputOutput.printUsersToFile();// upisivanje novog korisnika u
												// fajl users.txt
			adminMenu();
			break;
		}
		case 2: {
			deleteUser();// obrisi User-a
			FileInputOutput.printUsersToFile();// snimi listu Usera u fajl nakon
												// sto smo obrisali jednog Usera
			adminMenu();
			break;
		}
		case 3: {// provjeri stanje na bankomatu
			ATM.checkATM();
			adminMenu();
			break;
		}
		case 4: {
			setNumsOfPaperBills();// unosenje novih novcanica u bankomat
			FileInputOutput.printNumOfBillsToFile();// upis novog broja
													// novcanica u fajl
			adminMenu();
			break;
		}
		case 5:{//gasenje bankomata
			System.out.println("Bankomat ce se ugasiti.");
			System.exit(0);
		}
		default: {
			System.out.println("\nPogresan unos, pokusajte ponovo.");
			adminMenu();
		}
		}
	}

	/*
	 * pravljenje novog korisnika i dodavanje u userList
	 */
	public static void makeNewUser() {
		// privremena arrayLista user-a kojoj smo dodijelili prethodnu
		// userList-u
		ArrayList<User> newUserList = (ArrayList<User>) UserBase.getUserList();
		Scanner input = new Scanner(System.in);
		System.out.println("Unesite username novog korisnika:");
		String name = input.next();// username novog korisnika

		// boolean existingUser = checkForExistingUser(name);
		// provjera da li korisnik vec postoji i vrti petlju sve dok ne unese
		// username koji ne postoji u user listi
		while (checkForExistingUser(name)) {
			System.out
					.println("Username vec postoji, unesite drugi username: ");
			name = input.next();
		}
		System.out.println("Unesite password novog korisnika: ");
		String pass = input.next();
		// provjerava da li je password cetverocifren broj i vrti petlju dok ne
		// unese odgovarajuci pass
		while (!checkPassword(pass)) {
			System.out
					.println("Password mora biti cetverocifren broj, pokusajte ponovo: ");
			pass = input.next();
		}
		double balance = 0;// stanje racuna
		boolean isOK = false;// kontrola petlje
		while (!isOK) {
			/*
			 * ako admin ne unese double vrijednost trazi da ponovo unese
			 * vrijednost sve dok ne unese kako treba
			 */
		
		try {

			System.out.println("Unesite stanje racuna novog korisnika: ");
			balance = input.nextDouble();
			isOK = true;

		} catch (Exception a) {
			System.out.println("Pogresan unos!");
			isOK = false;
			input.nextLine();
		}
		}
		//pravi usera prosljedjivanjem name,pass i balance u kontruktor
		User account = new User(name, pass, balance);
		newUserList.add(account);// dodavanje novog korisnika u
									// privremenu listu
		UserBase.setUserList(newUserList);// setovanje prave userListe
											// prosledjivanjem liste sa
											// novim korisnikom
		System.out
				.println("\nUspjesno ste napravili novog korisnika: \nUsername: "
						+ name
						+ "\nPassword: "
						+ pass
						+ "\nBalance: "
						+ balance);

	}

	// brisanje korisnika
	public static void deleteUser() {
		// privremena lista koja sadrzi sve User-e iz userbase
		ArrayList<User> delUser = UserBase.getUserList();
		Scanner inputAgain = new Scanner(System.in);
		System.out.println("Unesite username korisnika: ");
		String delUsername = inputAgain.next();
		System.out
				.println("Potvrdite username korisnika kojeg zelite obrisati: ");
		String confirmUsername = inputAgain.next();
		if (delUsername.equals(confirmUsername)) {
			User user = UserBase.getUser(delUsername);
			if (user == null) {
				System.out.println("Ne postoji korisnik sa tim username-om.");
				deleteUser();
			} else {
				delUser.remove(user);// brisanje Usera iz privremene liste
				UserBase.setUserList(delUser);// postavljanje liste korisnika
												// prosledjivanjem privremene
												// liste
				System.out.println("Uspjesno ste obrisali korisnika:   "
						+ delUsername);
			}
		} else {
			System.out
					.println("Niste unijeli isti username, pokusajte ponovo!");
			deleteUser();
		}
	}

	// admin dodaje nove novcanice u bankomat
	static void setNumsOfPaperBills() {
		Scanner adminInput = new Scanner(System.in);
		// razlika izmedju limita i trenutnog broja novcanica
		int difference100 = ATM.billsLimit - ATM.getNumberOf100KMBills();
		int difference50 = ATM.billsLimit - ATM.getNumberOf50KMBills();
		int difference20 = ATM.billsLimit - ATM.getNumberOf20KMBills();
		int difference10 = ATM.billsLimit - ATM.getNumberOf10KMBills();
		int num100 = 0;
		int num50 = 0;
		int num20 = 0;
		int num10 = 0;
		boolean possible = false;
		while (!possible) {
			System.out
					.println("\nUnesite broj novcanica od 100 KM koje zelite dodati postojecim novcanicama:\n(mozete unijeti jos "
							+ difference100 + " novcanica)");
			num100 = adminInput.nextInt();
			if (num100 <= difference100) {
				possible = true;
			} else {
				System.out.println("Unijeli ste preveliki broj novcanica!");
			}
		}
		possible = false;
		while (!possible) {
			System.out
					.println("\nUnesite broj novcanica od 50 KM koje zelite dodati postojecim novcanicama:\n(mozete unijeti jos "
							+ difference50 + " novcanica)");
			num50 = adminInput.nextInt();
			if (num50 <= difference50) {
				possible = true;
			} else {
				System.out.println("Unijeli ste preveliki broj novcanica!");
			}
		}
		possible = false;
		while (!possible) {
			System.out
					.println("\nUnesite broj novcanica od 20 KM koje zelite dodati postojecim novcanicama:\n(mozete unijeti jos "
							+ difference20 + " novcanica)");
			num20 = adminInput.nextInt();
			if (num20 <= difference20) {
				possible = true;
			} else {
				System.out.println("Unijeli ste preveliki broj novcanica!");
			}
		}
		possible = false;
		while (!possible) {
			System.out
					.println("\nUnesite broj novcanica od 10 KM koje zelite dodati postojecim novcanicama:\n(mozete unijeti jos "
							+ difference10 + " novcanica)");
			num10 = adminInput.nextInt();
			if (num10 <= difference10) {
				possible = true;
			} else {
				System.out.println("Unijeli ste preveliki broj novcanica!");
			}
		}

		/*
		 * setovanje broja novcanica na novu vrijednost koja predstavlja zbir
		 * prethodnog broja novcanica i broja novcanica koje je unio admin
		 */
		ATM.setNumberOf100KMBills(ATM.getNumberOf100KMBills() + num100);
		ATM.setNumberOf50KMBills(ATM.getNumberOf50KMBills() + num50);
		ATM.setNumberOf20KMBills(ATM.getNumberOf20KMBills() + num20);
		ATM.setNumberOf10KMBills(ATM.getNumberOf10KMBills() + num10);
		System.out.println("\nUnos je uspjesno izvrsen.");

	}

	/*
	 * provjera da li postoji korisnik sa nekim username-om ukoliko postoji
	 * vraca true, a okoliko ne, vraca false
	 */
	public static boolean checkForExistingUser(String username) {
		boolean existingUser = false;
		for (int i = 0; i < UserBase.getUserList().size(); i++) {
			if (username.equals(UserBase.getUserList().get(i).getUserName())) {
				existingUser = true;
				break;
			} else {
				existingUser = false;
			}

		}
		return existingUser;
	}

	/*
	 * provjerava da li je password cetverocifren cijeli broj, ako jeste vracu
	 * true, ako nije, vraca false
	 */
	public static boolean checkPassword(String password) {
		boolean validPassword = true;
		char[] numArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		int countTrue = 0;
		if (password.length() == 4) {
			for (int i = 0; i < password.length(); i++) {
				for (int j = 0; j < numArray.length; j++) {
					if (password.charAt(i) == numArray[j]) {
						countTrue++;
					}
				}
			}
		}
		if (countTrue == 4) {
			validPassword = true;
		} else {
			validPassword = false;
		}
		return validPassword;
	}
}

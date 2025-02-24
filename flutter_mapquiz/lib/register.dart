import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'login.dart';
import 'package:hexcolor/hexcolor.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: RegisterPage(),
    );
  }
}

class RegisterPage extends StatefulWidget {
  @override
  _RegisterPageState createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final FirebaseAuth _auth = FirebaseAuth.instance;
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;

  String email = "", password = "", username = "";

  Future<bool> onBackPressed() {
    Navigator.push(context, MaterialPageRoute(builder: (_) => LoginPage()));
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: onBackPressed,
      child: Scaffold(
        backgroundColor: HexColor("#d1dbe2"),
        body: SingleChildScrollView(
          child: Column(
            children: <Widget>[
              Padding(
                padding: const EdgeInsets.only(top: 100, left: 15, bottom: 30),
                child: Container(
                  alignment: Alignment.center,
                  child: Text(
                    "Kaydol",
                    style: TextStyle(fontSize: 40, color: HexColor("#23435E")),
                    textAlign: TextAlign.left,
                  ),
                ),
              ),
              Padding(
                padding:
                    const EdgeInsets.only(left: 20.0, right: 20.0, top: 15),
                child: TextField(
                  onChanged: (String text) {
                    username = text;
                  },
                  decoration: InputDecoration(
                      border: OutlineInputBorder(),
                      hintText: 'Lütfen kullanıcı adınızı giriniz'),
                  cursorColor: const Color(0xff107163),
                ),
              ),
              Padding(
                padding:
                    const EdgeInsets.only(left: 20.0, right: 20.0, top: 15),
                child: TextField(
                  onChanged: (String text) {
                    email = text;
                  },
                  decoration: InputDecoration(
                      border: OutlineInputBorder(),
                      hintText: 'Lütfen e-posta giriniz'),
                  cursorColor: const Color(0xff107163),
                ),
              ),
              Padding(
                padding: const EdgeInsets.only(
                    left: 20.0, right: 20.0, top: 15, bottom: 30),
                child: TextField(
                  onChanged: (String text) {
                    password = text;
                  },
                  obscureText: true,
                  decoration: InputDecoration(
                      border: OutlineInputBorder(),
                      hintText: 'Lütfen şifrenizi giriniz'),
                ),
              ),
              Container(
                height: 50,
                width: 150,
                decoration: BoxDecoration(
                    color: HexColor("#23435E"),
                    borderRadius: BorderRadius.circular(5)),
                child: FlatButton(
                  onPressed: () async {
                    createPerson();
                  },
                  child: Text(
                    'Kaydol',
                    style: TextStyle(color: HexColor("#d1dbe2"), fontSize: 20),
                  ),
                ),
              ),
              SizedBox(
                height: 10,
              ),
              FlatButton(
                onPressed: () {
                  Navigator.push(
                      context, MaterialPageRoute(builder: (_) => LoginPage()));
                },
                child: Text(
                  'Hesabınız var mı? Giriş yapın.',
                  style: TextStyle(color: HexColor("#23435E"), fontSize: 15),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> createPerson() async {
    if (username.length == 0 || email.length == 0 || password.length == 0) {
      Fluttertoast.showToast(
          msg: 'Lütfen tüm boşlukları doldurun.',
          toastLength: Toast.LENGTH_SHORT,
          gravity: ToastGravity.BOTTOM);
    } else {
      final snapShot = await _firestore
          .collection('Kullanıcılar')
          .where('kullaniciAdi', isEqualTo: username)
          .get();

      final snapShot2 = await _firestore
          .collection('Kullanıcılar')
          .where('email', isEqualTo: email)
          .get();

      var b1 = snapShot.docs.isEmpty;
      var b2 = snapShot2.docs.isEmpty;

      if (!b1) {
        Fluttertoast.showToast(
            msg: 'Bu kullanıcı adı kullanılıyor.',
            toastLength: Toast.LENGTH_SHORT,
            gravity: ToastGravity.BOTTOM);
      } else if (!b2) {
        Fluttertoast.showToast(
            msg: 'Bu mail adresi kullanılıyor.',
            toastLength: Toast.LENGTH_SHORT,
            gravity: ToastGravity.BOTTOM);
      } else if (b1 && b2) {
        try {
          var user = await _auth.createUserWithEmailAndPassword(
              email: email, password: password);

          Fluttertoast.showToast(
              msg: 'Kayfolma işlemi başarılı.',
              toastLength: Toast.LENGTH_SHORT,
              gravity: ToastGravity.BOTTOM);

          addPerson(user);

          Navigator.push(
              context, MaterialPageRoute(builder: (_) => LoginPage()));
        } catch (e) {
          print(e.toString());

          Fluttertoast.showToast(
              msg: 'Hatalı bilgi girdiniz.',
              toastLength: Toast.LENGTH_SHORT,
              gravity: ToastGravity.BOTTOM);
        }
      }
    }
  }

  Future<void> addPerson(UserCredential user) async {
    await _firestore
        .collection("Kullanıcılar")
        .doc(user.user.uid)
        .set({'kullaniciAdi': username, 'email': email, 'puan': 0});
  }
}

import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:hexcolor/hexcolor.dart';
import 'package:mapquiz_web/info_list.dart';

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
      home: RegisterDemo(),
    );
  }
}

class RegisterDemo extends StatefulWidget {
  @override
  _RegisterDemoState createState() => _RegisterDemoState();
}

class _RegisterDemoState extends State<RegisterDemo> {
  TextEditingController statusController = TextEditingController();
  final auth = FirebaseAuth.instance;
  String password = '', username = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: HexColor("#d1dbe2"),
      body: SingleChildScrollView(
        child: Column(
          children: <Widget>[
            SizedBox(
              height: 60,
            ),
            Container(
              alignment: Alignment.center,
              child: Image.asset("images/globe2.png"),
            ),
            SizedBox(
              height: 50,
            ),
            Container(
              alignment: Alignment.center,
              height: 50,
              width: 300,
              child: TextField(
                onChanged: (String text) {
                  username = text;
                },
                decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    hintText: 'Lütfen kullanıcı adını giriniz'),
                cursorColor: const Color(0xff107163),
              ),
            ),
            SizedBox(
              height: 20,
            ),
            Container(
              alignment: Alignment.center,
              height: 50,
              width: 300,
              child: TextField(
                onChanged: (String text) {
                  password = text;
                },
                obscureText: true,
                decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    hintText: 'Lütfen şifreyi giriniz'),
              ),
            ),
            SizedBox(
              height: 15,
            ),
            Container(
              height: 50,
              width: 150,
              decoration: BoxDecoration(
                  color: HexColor("#23435E"),
                  borderRadius: BorderRadius.circular(5)),
              child: FlatButton(
                onPressed: () async {
                  check();
                },
                child: Text(
                  'Giriş Yap',
                  style: TextStyle(color: HexColor("#d1dbe2"), fontSize: 20),
                ),
              ),
            ),
            SizedBox(
              height: 15,
            ),
            TextButton(
              onPressed: () {
                resetPassword(context);
              },
              child: Text('Şifremi unuttum',
                  style: TextStyle(color: HexColor("#23435E"), fontSize: 15)),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> resetPassword(BuildContext context) {
    String _email = "";

    return showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
              title: Text(
                "Şifre Yenileme",
                textAlign: TextAlign.center,
                style: TextStyle(color: HexColor("#23435E")),
              ),
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(8.0))),
              content: Container(
                height: 150,
                width: 300,
                decoration: BoxDecoration(
                  color: Colors.white,
                ),
                child: Column(
                  children: <Widget>[
                    SizedBox(height: 20),
                    Row(
                      mainAxisSize: MainAxisSize.max,
                      children: <Widget>[
                        Text(
                          "Email:",
                          style: TextStyle(
                              fontSize: 20, color: HexColor("#23435E")),
                          textAlign: TextAlign.center,
                        ),
                        SizedBox(width: 20.0),
                        Container(
                          height: 50,
                          width: 220,
                          child: TextField(
                            onChanged: (String text) {
                              setState(() {
                                _email = text;
                              });
                            },
                            decoration: InputDecoration(
                                border: OutlineInputBorder(),
                                hintText: 'Email adresinizi giriniz'),
                          ),
                        ),
                      ],
                    ),
                    SizedBox(
                      height: 40,
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: <Widget>[
                        GestureDetector(
                          onTap: () {
                            bool flag = RegExp(
                                    r"^[a-zA-Z0-9.a-zA-Z0-9.!#$%&'*+-/=?^_`{|}~]+@[a-zA-Z0-9]+\.[a-zA-Z]+")
                                .hasMatch(_email);
                            if (flag) {
                              auth.sendPasswordResetEmail(email: _email);
                              Fluttertoast.showToast(
                                  msg: 'Mail gönderildi.',
                                  toastLength: Toast.LENGTH_SHORT,
                                  webPosition: "center",
                                  webBgColor: "ff90a4ae");
                              Navigator.pop(context);
                            } else {
                              Fluttertoast.showToast(
                                  msg:
                                      'Eksik ya da hatalı mail adresi girdiniz',
                                  toastLength: Toast.LENGTH_SHORT,
                                  webPosition: "center",
                                  webBgColor: "ff90a4ae");
                            }
                          },
                          child: Text(
                            "Gönder",
                            style: TextStyle(
                                color: HexColor("#23435E"),
                                fontWeight: FontWeight.bold),
                          ),
                        ),
                        GestureDetector(
                          onTap: () {
                            Navigator.pop(context);
                          },
                          child: Text(
                            "Vazgeç",
                            style: TextStyle(
                                color: HexColor("#23435E"),
                                fontWeight: FontWeight.bold),
                          ),
                        ),
                      ],
                    )
                  ],
                ),
              ));
        });
  }

  Future<void> check() async {
    String _username, _email;

    if (username.length == 0 || password.length == 0) {
      Fluttertoast.showToast(
          msg: 'Eksik bilgi girdiniz.',
          toastLength: Toast.LENGTH_SHORT,
          webPosition: "center",
          webBgColor: "ff90a4ae");
    } else {
      FirebaseFirestore.instance
          .collection('Admin')
          .get()
          .then((QuerySnapshot querySnapshot) {
        querySnapshot.docs.forEach((doc) {
          _username = doc['kullaniciAdi'];
          _email = doc['email'];

          if (username == _username) {
            try {
              FirebaseAuth.instance.signInWithEmailAndPassword(
                  email: _email, password: password);
              Navigator.push(
                  context, MaterialPageRoute(builder: (_) => InfoListPage()));
            } catch (error) {
              Fluttertoast.showToast(
                  msg: 'Hatalı bilgi girdiniz.',
                  toastLength: Toast.LENGTH_SHORT,
                  webPosition: "center",
                  webBgColor: "ff90a4ae");
            }
          } else {
            Fluttertoast.showToast(
                msg: 'Hatalı bilgi girdiniz.',
                toastLength: Toast.LENGTH_SHORT,
                webPosition: "center",
                webBgColor: "ff90a4ae");
          }
        });
      });
    }
  }
}

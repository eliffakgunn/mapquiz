import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter_mapquiz/quiz_home.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:hexcolor/hexcolor.dart';
import 'register.dart';
import 'quiz.dart';

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
      home: LoginPage(),
    );
  }
}

class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final auth = FirebaseAuth.instance;
  String email, password;

  Future<bool> onBackPressed() {
    Navigator.push(context, MaterialPageRoute(builder: (_) => QuizHomePage()));
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
                    "Giriş Yap",
                    style: TextStyle(fontSize: 40, color: HexColor("#23435E")),
                    textAlign: TextAlign.center,
                  ),
                ),
              ),
              Padding(
                padding: EdgeInsets.symmetric(horizontal: 20),
                child: TextField(
                  onChanged: (String text) {
                    email = text;
                  },
                  decoration: InputDecoration(
                      border: OutlineInputBorder(),
                      hintText: 'Lütfen mail adresinizi giriniz'),
                  cursorColor: const Color(0xff107163),
                ),
              ),
              Padding(
                padding: const EdgeInsets.only(
                    left: 20.0, right: 20.0, top: 15),
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
              SizedBox(
                height: 5,
              ),
              TextButton(
                onPressed: () {
                  resetPassword(context);
                },
                child: Text('Şifremi unuttum',
                    style: TextStyle(color: HexColor("#23435E"), fontSize: 15)),
              ),
              SizedBox(
                height: 30,
              ),
              Container(
                height: 50,
                width: 150,
                decoration: BoxDecoration(
                    color: HexColor("#23435E"),
                    borderRadius: BorderRadius.circular(5)),
                child: FlatButton(
                  onPressed: () async {
                    signIn();
                  },
                  child: Text(
                    'Giriş Yap',
                    style: TextStyle(color: HexColor("#d1dbe2"), fontSize: 20),
                  ),
                ),
              ),
              SizedBox(
                height: 10,
              ),
              FlatButton(
                onPressed: () {
                  Navigator.push(context,
                      MaterialPageRoute(builder: (_) => RegisterPage()));
                },
                child: Text(
                  'Hesabınız yok mu? Kaydolun.',
                  style: TextStyle(color: HexColor("#23435E"), fontSize: 15),
                ),
              ),
            ],
          ),
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
                              fontSize: 18, color: HexColor("#23435E")),
                          textAlign: TextAlign.center,
                        ),
                        SizedBox(width: 20.0),
                        Container(
                          height: 50,
                          width: 200,
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

  Future<void> signIn() async {
    await Firebase.initializeApp();
    print("signIn");
    try {
      UserCredential user = await auth.signInWithEmailAndPassword(
          email: email, password: password);
      if (user != null) {
        print("null değil");
        Navigator.push(context, MaterialPageRoute(builder: (_) => QuizPage()));
      } else {
        print("null");
        Fluttertoast.showToast(
            msg: 'Eksik ya da hatalı bilgi girdiniz.',
            toastLength: Toast.LENGTH_SHORT,
            gravity: ToastGravity.BOTTOM);
      }
    } catch (e) {
      print("error");
      print(e.toString());

      Fluttertoast.showToast(
          msg: 'Eksik ya da hatalı bilgi girdiniz.',
          toastLength: Toast.LENGTH_SHORT,
          gravity: ToastGravity.BOTTOM);
    }
  }
}

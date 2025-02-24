import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter_mapquiz/quiz_home.dart';
import 'package:hexcolor/hexcolor.dart';
import 'package:flutter/services.dart';

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
      home: Scaffold(body: HomePage()),
    );
  }
}

class HomePage extends StatelessWidget {
  HomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  Widget build(BuildContext context) {
    return _HomePage(context);
  }
}

Widget _HomePage(BuildContext context) {
  const platform = const MethodChannel("com.flutter.mapquiz/mapquiz");

  final loginButton = Material(
    elevation: 5.0,
    borderRadius: BorderRadius.circular(5.0),
    color: HexColor("#23435E"),
    child: MaterialButton(
      minWidth: MediaQuery.of(context).size.width,
      padding: EdgeInsets.fromLTRB(70.0, 15.0, 70.0, 15.0),
      onPressed: () async {
        String value;
        try {
          value = await platform.invokeMethod("Kesfet");
        } catch (e) {
          print(e);
        }
      },
      child: Text(
        "Keşfet",
        textAlign: TextAlign.center,
        style: TextStyle(
            fontSize: 20,
            color: HexColor("#d1dbe2"),
            fontStyle: FontStyle.normal),
      ),
    ),
  );

  final imgGlobe = Material(
    child: Image.asset("images/globe2.png"),
  );

  final registerButton = Material(
    elevation: 5.0,
    borderRadius: BorderRadius.circular(5.0),
    color: HexColor("#23435E"),
    child: MaterialButton(
      minWidth: MediaQuery.of(context).size.width,
      padding: EdgeInsets.fromLTRB(70.0, 15.0, 70.0, 15.0),
      onPressed: () {
        Navigator.push(
            context, MaterialPageRoute(builder: (_) => QuizHomePage()));
      },
      child: Text(
        "Quiz",
        textAlign: TextAlign.center,
        style: TextStyle(
            fontSize: 20,
            color: HexColor("#d1dbe2"),
            fontStyle: FontStyle.normal),
      ),
    ),
  );

  return Center(
    child: Container(
      color: HexColor("#d1dbe2"),
      child: Padding(
        padding: EdgeInsets.fromLTRB(50, 0, 50, 100),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            imgGlobe,
            SizedBox(height: 80.0),
            loginButton,
            SizedBox(height: 25.0),
            registerButton
          ],
        ),
      ),
    ),
  );
}

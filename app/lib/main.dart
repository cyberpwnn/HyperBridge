import 'dart:ui';

import 'package:app/ui/hyperbridge.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Flutter Demo',
      theme: ThemeData(
          canvasColor: Colors.black,
          bottomAppBarColor: Colors.black,
          iconTheme: IconThemeData(
            color: Colors.white,
          ),
          appBarTheme:
              AppBarTheme(color: Colors.black, shadowColor: Colors.black),
          primarySwatch: Colors.deepPurple,
          brightness: Brightness.dark),
      home: HyperBridge(),
    );
  }
}

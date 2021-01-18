import 'dart:ui';

import 'package:app/network.dart';
import 'package:flutter/material.dart';
import 'package:tinycolor/tinycolor.dart';

class LightTile extends StatefulWidget {
  final String light;
  final Color background;
  final VoidCallback onTap;

  LightTile({this.light, this.onTap, this.background});

  @override
  _LightTileState createState() => _LightTileState();
}

class _LightTileState extends State<LightTile> {
  LightData temp;
  int last = DateTime.now().millisecondsSinceEpoch;

  Future<LightData> get() async {
    if (temp != null) {
      return temp;
    }

    return Network.getLight(widget.light);
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<LightData>(
      future: get(),
      builder: (context, snap) {
        if (!snap.hasData) {
          return Container();
        }
        temp = null;
        LightData d = snap.data;
        int nightlightmode = 60;
        TinyColor buffer = TinyColor(d.getColor());
        return Padding(
          padding: EdgeInsets.all(7),
          child: InkWell(
            child: GestureDetector(
              onPanUpdate: (a) {
                buffer = buffer.spin(a.delta.dx).saturate(a.delta.dy.toInt());
                d.r = buffer.color.red;
                d.g = buffer.color.green;
                d.b = buffer.color.blue;
                if (DateTime.now().millisecondsSinceEpoch - last < 250) {
                  setState(() {
                    temp = d;
                  });
                } else {
                  last = DateTime.now().millisecondsSinceEpoch;
                  Network.setColor(d.id,
                          color: buffer.color,
                          brightness: d.a / 255.0,
                          now: true,
                          ms: 250)
                      .then((value) => setState(() {}));
                }
              },
              onPanEnd: (b) {
                Future.delayed(
                    Duration(milliseconds: 500),
                    () => Network.setColor(d.id,
                            color: buffer.color,
                            brightness: d.a / 255.0,
                            ms: 1000)
                        .then((value) => setState(() {})));
              },
              child: Container(
                color: widget.background,
                width: 200,
                height: 200,
                child: Center(
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      Align(
                        alignment: Alignment.topCenter,
                        child: Text(
                            d.a > nightlightmode ? "\u{e811}" : "\u{f2ab}",
                            textAlign: TextAlign.center,
                            style: TextStyle(
                                fontFamily: "MaterialIcons",
                                color: d.getColor(alphaBrightness: true),
                                fontSize: 65,
                                height: 1.7,
                                shadows: <Shadow>[
                                  Shadow(
                                    color: d.getColor(alphaBrightness: true),
                                    blurRadius: lerpDouble(
                                        0,
                                        40,
                                        (d.a > nightlightmode
                                            ? (d.a.toDouble() / 255.0)
                                            : (d.a.toDouble() /
                                                nightlightmode.toDouble()))),
                                  )
                                ])),
                      ),
                      Align(
                        alignment: Alignment.bottomCenter,
                        child: Text(
                          d.name,
                          textAlign: TextAlign.center,
                          style: TextStyle(fontSize: 24),
                        ),
                      )
                    ],
                  ),
                ),
              ),
            ),
            splashColor: d.getColor(alphaBrightness: false),
            onTap: widget.onTap == null
                ? () {
                    Network.setColorRaw(d.id,
                            r: d.r,
                            g: d.g,
                            b: d.b,
                            a: d.a > 60 ? 30 : 255,
                            ms: 250)
                        .then((value) => setState(() {}));
                  }
                : widget.onTap,
            onLongPress: widget.onTap != null
                ? () {
                    Network.setColorRaw(d.id,
                            r: d.r,
                            g: d.g,
                            b: d.b,
                            a: d.a > 60 ? 30 : 255,
                            ms: 250)
                        .then((value) => setState(() {}));
                  }
                : null,
          ),
        );
      },
    );
  }
}

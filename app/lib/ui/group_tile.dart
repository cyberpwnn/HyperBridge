import 'dart:ui';

import 'package:app/network.dart';
import 'package:flutter/material.dart';

class GroupTile extends StatefulWidget {
  final bool texts;
  final String group;
  final VoidCallback onTap;

  GroupTile({this.group, this.onTap, this.texts = true});

  @override
  _GroupTileState createState() => _GroupTileState();
}

class _GroupTileState extends State<GroupTile> {
  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: widget.onTap,
      child: Container(
        width: 200,
        height: 200,
        child: Stack(
          fit: StackFit.passthrough,
          children: [
            widget.texts ? Container() : buildGradient(),
            widget.texts
                ? Container()
                : Container(
                    child: BackdropFilter(
                      filter: ImageFilter.blur(sigmaX: 55, sigmaY: 55),
                      child: Container(
                        color: Colors.transparent,
                      ),
                    ),
                  ),
            !widget.texts
                ? Container()
                : FutureBuilder<GroupData>(
                    future: Network.getGroup(widget.group),
                    builder: (context, snap2) {
                      if (!snap2.hasData) {
                        return Center(
                          child: CircularProgressIndicator(
                            valueColor:
                                AlwaysStoppedAnimation(Colors.deepPurple),
                          ),
                        );
                      }

                      return Center(
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              snap2.data.name,
                              style: TextStyle(fontSize: 24),
                            ),
                            Text(snap2.data.lights.length.toString() +
                                " Lights"),
                            Text(snap2.data.watts.round().toString() + "W"),
                          ],
                        ),
                      );
                    },
                  ),
          ],
        ),
      ),
    );
  }

  Future<List<LightData>> getLightData() async {
    GroupData d = await Network.getGroup(widget.group);
    List<LightData> l = List<LightData>();

    for (int i = 0; i < d.lights.length; i++) {
      l.add(await Network.getLight(d.lights[i]));
    }

    return l;
  }

  Widget buildGradient() {
    return FutureBuilder<List<LightData>>(
      future: getLightData(),
      builder: (context, snap) {
        if (!snap.hasData) {
          return Container();
        }

        List<Color> colors = List<Color>();
        snap.data.forEach((element) {
          colors.add(element.getColor(alphaBrightness: true));
        });

        colors.sort((a, b) => b.alpha - a.alpha);

        return Container(
          color: colors.length == 1 ? colors[0] : null,
          decoration: colors.length > 1
              ? BoxDecoration(gradient: RadialGradient(colors: colors))
              : null,
        );
      },
    );
  }
}

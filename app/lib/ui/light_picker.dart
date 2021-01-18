import 'package:app/network.dart';
import 'package:app/ui/light_tile.dart';
import 'package:flutter/material.dart';

class LightPicker extends StatefulWidget {
  final List<String> checked;

  LightPicker({this.checked});

  @override
  _LightPickerState createState() => _LightPickerState(ccc: checked);
}

class _LightPickerState extends State<LightPicker> {
  List<String> ccc = List<String>();

  _LightPickerState({this.ccc});

  @override
  Widget build(BuildContext context) {
    if (ccc == null) {
      ccc = List<String>();
    }

    return Scaffold(
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.check),
        backgroundColor: Colors.deepPurple,
        onPressed: () {
          Navigator.pop(context, ccc);
        },
      ),
      body: FutureBuilder<List<String>>(
        future: Network.getLights(),
        builder: (context, snp) {
          if (!snp.hasData) {
            return Center(
              child: CircularProgressIndicator(
                valueColor: AlwaysStoppedAnimation(Colors.deepPurple),
              ),
            );
          }

          return GridView.builder(
            itemCount: snp.data.length,
            padding: EdgeInsets.all(7),
            gridDelegate: SliverGridDelegateWithMaxCrossAxisExtent(
                maxCrossAxisExtent: 200),
            itemBuilder: (context, pos) => LightTile(
              light: snp.data[pos],
              onTap: () => setState(() {
                if (!ccc.contains(snp.data[pos])) {
                  ccc.add(snp.data[pos]);
                } else {
                  ccc.removeWhere((i) => i == snp.data[pos]);
                }
              }),
              background: ccc.contains(snp.data[pos])
                  ? Colors.deepPurple.withAlpha(128)
                  : null,
            ),
          );
        },
      ),
    );
  }
}

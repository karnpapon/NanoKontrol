// =========================================================
// Title         : NanoKontrol
// Description   : Controller class for Korg NanoKONTROL
// Version       : 1.0
// =========================================================


// TODO: implement scene btn.
NanoKontrol {
    var <faders, <knobs;
    var <upBtns, <downBtns;

    var ctls, midiOut;
    var ccFaders, ccKnobs;
    var ccUpBtns, ccDownBtns;
    var ccTransportBtns, ccCycleBtn;

    *new {
        ^super.new.init;
    }

    init {
        ctls = ();

        faders = List[];
        knobs  = List[];

        upBtns  = List[];
        downBtns  = List[];

        ccFaders = (2..6)++[8,9]++[12,13];
        ccKnobs  = (14..22);
        ccUpBtns  = (23..31);
        ccDownBtns  = (33..41);

        ccTransportBtns = (44..48);
        ccCycleBtn      = 49;

        MIDIClient.init;
        MIDIIn.connectAll;

        this.assignCtls;
    }

    assignCtls {
        ccFaders.do {|cc, i|
            var key = ("fader" ++ (i+1)).asSymbol;
            var nk  = NKController(key, cc);
            faders.add(nk);
            ctls.put(key, nk);
        };

        ccKnobs.do {|cc, i|
            var key = ("knob" ++ (i+1)).asSymbol;
            var nk  = NKController(key, cc);
            knobs.add(nk);
            ctls.put(key, nk);
        };

        ccUpBtns.collect {|cc, i|
            var key = ("upBtn" ++ (i+1)).asSymbol;
            var nk  = NKButton(key, cc, midiOut);
            upBtns.add(nk);
            ctls.put(key, nk);
        };

        ccDownBtns.collect {|cc, i|
            var key = ("downBtn" ++ (i+1)).asSymbol;
            var nk  = NKButton(key, cc, midiOut);
            downBtns.add(nk);
            ctls.put(key, nk);
        };

        [ [ 'recBtn', 'playBtn', 'stopBtn', 'bwBtn', 'fwdBtn' ], ccTransportBtns ].flopWith {|key, cc|
            ctls.put(key, NKButton(key, cc, midiOut));
        };

        ctls.put('cycleBtn', NKButton('cycleBtn', ccCycleBtn, midiOut));
    }

    freeAll {
        ctls.do(_.free);
    }

    doesNotUnderstand {|selector ... args|
        ^ctls[selector] ?? { ^super.doesNotUnderstand(selector, args) }
    }
}

NKController {
    var key, cc, midiOut;

    *new {|key, cc|
        ^super.newCopyArgs(("nk_" ++ key).asSymbol, cc);
    }

    onChange_ {|func|
        MIDIdef.cc(key, func, cc);
    }

    free {
        MIDIdef.cc(key).free;
    }
}

NKButton : NKController {
    var key, cc;

    *new {|key, cc, aMidiOut|
        ^super.newCopyArgs(("nk_" ++ key).asSymbol, cc, aMidiOut);
    }

    onPress_ {|func|
        MIDIdef.cc((key ++ "_on").asSymbol, {|val|
            if (val == 127) {
                func.(val, this)
            }
        }, cc);
    }

    onRelease_ {|func|
        MIDIdef.cc((key ++ "_off").asSymbol, {|val|
            if (val == 0) {
                func.(val, this)
            }
        }, cc);
    }
}

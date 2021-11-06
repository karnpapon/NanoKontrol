// =========================================================
// Title         : NanoKontrol
// Description   : Controller class for Korg NanoKONTROL
// Version       : 1.0
// =========================================================


// TODO: implement scene btn.
NanoKontrol {
    var <faders, <knobs;
    var <sBtns, <rBtns;
    // var <>ledMode;

    var ctls, midiOut;
    var ccFaders, ccKnobs;
    var ccSBtns, ccRBtns;
    var ccTransportBtns, ccCycleBtn;

    *new {|ledMode=\internal|
        ^super.new.ledMode_(ledMode).init;
    }

    init {
        ctls = ();

        faders = List[];
        knobs  = List[];

        sBtns  = List[];
        rBtns  = List[];

        ccFaders = [2,3,4,5,6,8,9,12,13];
        ccKnobs  = (14..22);
        ccSBtns  = (23..31);
        ccRBtns  = (33..41);

        ccTransportBtns = [ 45, 46, 44, 47, 48 ];
        ccCycleBtn      = 49;

        MIDIClient.init;
        MIDIIn.connectAll;

        // if(ledMode == \external) {
        //     // Device/Port name might have to be edited to match your setup.
        //     midiOut = MIDIOut.newByName("nanoKONTROL", "CTRL");
        // };

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

        ccSBtns.collect {|cc, i|
            var key = ("sBtn" ++ (i+1)).asSymbol;
            var nk  = NKButton(key, cc, midiOut);
            sBtns.add(nk);
            ctls.put(key, nk);
        };

        ccMBtns.collect {|cc, i|
            var key = ("mBtn" ++ (i+1)).asSymbol;
            var nk  = NKButton(key, cc, midiOut);
            mBtns.add(nk);
            ctls.put(key, nk);
        };

        ccRBtns.collect {|cc, i|
            var key = ("rBtn" ++ (i+1)).asSymbol;
            var nk  = NKButton(key, cc, midiOut);
            rBtns.add(nk);
            ctls.put(key, nk);
        };

        [ [ 'playBtn', 'stopBtn', 'recBtn', 'bwBtn', 'fwdBtn' ], ccTransportBtns ].flopWith {|key, cc|
            ctls.put(key, NKButton(key, cc, midiOut));
        };

        ctls.put('cycleBtn', NKButton('cycleBtn', ccCycleBtn, midiOut));
    }

    freeAll {
        ctls.do(_.free);
    }

    ledsOff {
        ctls.do(_.ledOff);
    }

    doesNotUnderstand {|selector ... args|
        ^ctls[selector] ?? { ^super.doesNotUnderstand(selector, args) }
    }
}

NKController {
    var key, cc, midiOut;
    var state = 0;

    *new {|key, cc|
        ^super.newCopyArgs(("nk_" ++ key).asSymbol, cc);
    }

    onChange_ {|func|
        MIDIdef.cc(key, func, cc);
    }

    ledOff {
        midiOut !? {
            midiOut.control(0, cc, 0);
            state = 0;
        };
    }

    free {
        MIDIdef.cc(key).free;
        this.ledOff;
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

    // ledState {
    //     ^state;
    // }

    // ledState_ {|val|
    //     val   = val.clip(0, 1);
    //     state = val;

    //     midiOut !? {
    //         midiOut.control(0, cc, 127 * val);
    //     };
    // }
}

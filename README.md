# `NanoKontrol` for `SuperCollider`

Interface for using Korg NanoKontrol with SuperCollider.

Basic usage
-----------

```
n = NanoKontrol();

// register a function to be evaluted when fader1 is changed
n.fader1.onChange = {|val| (val/127).postln; }

// overwrite the previous assignment
n.fader1.onChange = {|val| val.linexp(0, 127, 20, 20000).postln; }

n.upBtn1.onPress = { "Hello, ".post; };
n.upBtn1.onRelease = { "NanoKONTROL!".postln; };
```

Incremental assignment
----------------------

It is possible to incrementally assign faders, knobs, and the fader buttons.

```
n = NanoKontrol();

(
n.faders.do {|fader, i|
    fader.onChange = {|val|
        "This is fader % its value is %\n".postf(i+1, val);
    }
};

n.knobs.do {|knob, i|
    knob.onChange = {|val|
        "This is knob % its value is %\n".postf(i+1, val);
    }
};

n.downBtns.do {|rBtn, i|
    downBtn.onChange = {|val|
        "This is rBtn % its value is %\n".postf(i+1, val);
    }
};
)

```

Or just a selection of controls
```
// assign faders 1 .. 4
n.faders[..3].do {|fader, i| 
    fader.onChange = {|val|
        "This is fader % its value is %\n".postf(i+1, val);
    }
};
```

Interface
---------

### Methods

`onChange` all controls (faders/knobs/buttons) can register a function using this method

`onPress` only register press on buttons

`onRelease` only register release on buttons

`free` unregisters a MIDI responder

`freeAll` unregisters all MIDI responders

`ledsOff` turn off all LEDs

*Note: `Cmd-.` removes all MIDI responders by default in SuperCollider*

### Controller names

All controls on the NanoKONTROL are supported, see list of names below.

#### Faders/Knobs

* `fader1 .. 8`
* `knob1 .. 8`

#### Buttons

* `upBtn1 .. 8`
* `downBtn1 .. 8`

#### Transport buttons

* `bwBtn`
* `fwdBtn` 
* `stopBtn` 
* `playBtn` 
* `recBtn`
* `cycleBtn`

#### Collections

* `faders`
* `knobs`
* `upBtns`
* `downBtns`

## Credits

Based on `NanoKontrol2` by  [David Granström](https://github.com/davidgranstrom/NanoKontrol2)
// testing folder

var describe: any;
var it: any;
var expect: any;

// first test to check that clicking post button hides listing
it("UI Test: Add Button Hides Listing", function(){
    // click the button for showing the add button
    (<HTMLElement>document.getElementById("showAdd")).click();
    // expect that the add form is not hidden
    expect( (<HTMLElement>document.getElementById("addElement")).style.display ).toEqual("block");
    // expect that the element listing is hidden
    expect( (<HTMLElement>document.getElementById("showElements")).style.display ).toEqual("none");
    // reset the UI, so we don't mess up the next test
(<HTMLElement>document.getElementById("addCancel")).click();  
});


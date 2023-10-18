// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $: any;

// The 'this' keyword does not behave in JavaScript/TypeScript like it does in
// Java.  Since there is only one NewEntryForm, we will save it to a global, so
// that we can reference it from methods of the NewEntryForm in situations where
// 'this' won't work correctly.
var newEntryForm: NewEntryForm;

// a global for the main ElementList of the program.  See newEntryForm for 
// explanation
var mainList: ElementList;


// a global for the EditEntryForm of the program.  See newEntryForm for explanation
var editEntryForm: EditEntryForm;

// variable to keep track of if like was clicked
var likeClicked = false;

// global for the backend url
const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";

/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */
class NewEntryForm {
    /**
     * To initialize the object, we say what method of NewEntryForm should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
        document.getElementById("addButton")?.addEventListener("click", (e) => { newEntryForm.submitForm(); });

    }

    /**
     * Clear the form's input fields
     */
    clearForm() {
        (<HTMLInputElement>document.getElementById("newIdea")).value = "";

                // reset the UI
                //(<HTMLElement>document.getElementById("editElement")).style.display = "none";
                (<HTMLElement>document.getElementById("addElement")).style.display = "none";
                (<HTMLElement>document.getElementById("showElements")).style.display = "block";
    }

    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitForm() {
        //window.alert("Submit form called.");
        // get the values of the idea field, force them to be strings, and check 
        // that neither is empty
        let idea = "" + (<HTMLInputElement>document.getElementById("newIdea")).value;
        if (idea === "") {
            window.alert("Error: idea is not valid");
            return;
        }

        // set up an AJAX POST. 
        // When the server replies, the result will go to onSubmitResponse
        const doAjax = async () => {
            await fetch(`${backendUrl}/ideas`, {
                method: 'POST',
                body: JSON.stringify({
                    mContent: idea,
                    mLikeCount: 0
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then((response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                // Otherwise, handle server errors with a detailed popup message
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                newEntryForm.onSubmitResponse(data);
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong with POST.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form
        if (data.mStatus === "ok") {
            //window.alert("onSubmitResponse = ok" + data);
            newEntryForm.clearForm();
            mainList.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.ideas);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error returned");
        }
    }
} // end class NewEntryForm

/**
 * ElementList provides a way of seeing all of the data stored on the server.
 */
class ElementList {
    /**
     * refresh is the public method for updating ideaList
     */
    refresh() {
        // Issue an AJAX GET and then pass the result to update(). 
        const doAjax = async () => {
            await fetch(`${backendUrl}/ideas`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then((response) => {
                // If we get an "ok" idea, clear the form
                if (response.ok) {
                    console.log("made it this far");
                    return Promise.resolve(response.json());

                }
                // Otherwise, handle server errors with a detailed popup idea
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                mainList.update(data);
                console.log("DATAAA");
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong with GET.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    private update(data: any) {
        let elem_ideaList = document.getElementById("ideaList");

        if (elem_ideaList !== null) {
            elem_ideaList.innerHTML = "";

            let fragment = document.createDocumentFragment();
            let table = document.createElement('table');
            table.setAttribute('id', 'ideaTable')
            for (let i = 0; i < data.mData.length; ++i) {
                let tr = document.createElement('tr');
                let td_message = document.createElement('td');
                let td_id = document.createElement('td');
                td_id.setAttribute('id', 'id_val');
                let td_like = document.createElement('td');
                td_id.innerHTML = data.mData[i].mId;
                td_message.innerHTML = data.mData[i].mContent;
                td_like.innerHTML = data.mData[i].mLikeCount;
                tr.appendChild(td_id);
                tr.appendChild(td_message);
                tr.appendChild(this.likeButtons(data.mData[i].mId));
                tr.appendChild(td_like);
                tr.appendChild(this.deleteButtons(data.mData[i].mId));
                table.appendChild(tr);
            }
            fragment.appendChild(table);

            elem_ideaList.appendChild(fragment);
        }

        // Find all of the delete buttons, and set their behavior
        const all_delbtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("delbtn"));
        for (let i = 0; i < all_delbtns.length; ++i) {
            all_delbtns[i].addEventListener("click", (e) => { mainList.clickDelete(e); });
        }

        // Find all of the like buttons, and set their behavior
        const all_likebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("likebtn"));
        for (let i = 0; i < all_likebtns.length; ++i) {
            all_likebtns[i].setAttribute('id', 'likeId');
            all_likebtns[i].addEventListener("click", (e) => { mainList.addLike(e); });
        }

        const all_dislikebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("likebtn"));
        for (let i = 0; i < all_likebtns.length; ++i) {
            all_dislikebtns[i].setAttribute('id', 'dislikeId');
            all_dislikebtns[i].addEventListener("click", (e) => { mainList.addLike(e); });
        }
    }

    /**
     * clickDelete is the code we run in response to a click of a delete button
     */
    private clickDelete(e: Event) {
        const id = (<HTMLElement>e.target).getAttribute("data-value");

        // Issue an AJAX DELETE and then invoke refresh()
        const doAjax = async () => {
            await fetch(`${backendUrl}/ideas/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then((response) => {
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                mainList.refresh();
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

/**
 * addLike is the code we run in response to a click of a delete button
 */
    private addLike(e: Event) {
        console.log("addLike");
        // as in clickDelete, we need the ID of the row
        const id = (<HTMLElement>e.target).getAttribute("data-value");
        // Issue an AJAX GET and then pass the result to editEntryForm.init()
        const doAjax = async () => {
            await fetch(`${backendUrl}/ideas/${id}`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then((response) => {
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                editEntryForm.init(data);
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error, on click edit");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    //  adding dislike functionality
    private addDisLike(e: Event) {
        console.log("addLike");
        // as in clickDelete, we need the ID of the row
        const id = (<HTMLElement>e.target).getAttribute("data-value");
        // Issue an AJAX GET and then pass the result to editEntryForm.init()
        const doAjax = async () => {
            await fetch(`${backendUrl}/ideas/${id}`, {
                method: 'GET',
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then((response) => {
                if (response.ok) {
                    return Promise.resolve(response.json());
                }
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then((data) => {
                editEntryForm.init(data);
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error, on click edit");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }
    


    /**
     * buttons() adds a button to the HTML for each row
     *
     * doesn't do anything yet
     */
    private likeButtons(id: string): DocumentFragment {
        let fragment = document.createDocumentFragment();
        let td = document.createElement('td');

        // create like button, add to new td, add td to returned fragment
        let btn = document.createElement('button');
        btn.classList.add("likebtn");
        btn.setAttribute('data-value', id);
        btn.innerHTML = 'like';
        td.appendChild(btn);
        fragment.appendChild(td);

        // create dislike button
        btn = document.createElement('button');
        btn.classList.add("dislikebtn");
        btn.setAttribute('data-value', id);
        btn.innerHTML = 'dislike';
        td.appendChild(btn);
        fragment.appendChild(td);

        return fragment;
    }

    // delete buttons
    private deleteButtons(id: string): DocumentFragment {
        let fragment = document.createDocumentFragment();
        let td = document.createElement('td');
        let btn = document.createElement('button');

        // create delete button, add to new td, add td to returned fragment
        td = document.createElement('td');
        btn = document.createElement('button');
        btn.classList.add("delbtn");
        btn.setAttribute('data-value', id);
        btn.innerHTML = 'Delete';
        td.appendChild(btn);
        fragment.appendChild(td);

        return fragment;
    }
} // end class ElementList


/**
 * EditEntryForm encapsulates all of the code for the form for editing an entry in terms of like & dislike
 */

class EditEntryForm {
    /**
     * To initialize the object, we say what method of EditEntryForm should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
        document.getElementById("likeId")?.addEventListener("click", (e) => { newEntryForm.submitForm(); });
    }

    /**
     * init() is called from an AJAX GET, and should populate the form if and 
     * only if the GET did not have an error
     */
    init(data: any) {
        console.log("init");
        likeClicked = true;
        // If we get an "ok" message, fill in the edit form
        if (data.mStatus === "ok") {
            console.log("init ok");
            // show the edit form
            // increasing like amount
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error, on init method");
        }
    }

    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitForm() {
        //window.alert("Submit edit form called.");
        // get the values of the two fields, force them to be strings, and check
        // that neither is empty
        let idea = "" + (<HTMLInputElement>document.getElementById("editMessage")).value;
        // NB: we assume that the user didn't modify the value of editId
        let id = "" + (<HTMLInputElement>document.getElementById("id_val")).value;
        let  prevLike = (<HTMLInputElement>document.getElementById("likeId")).value;
        console.log(prevLike);
        let like = (<HTMLInputElement>document.getElementById("likeId")).value +1;
        console.log("like button clicked, new like count = "+like);
        console.log(idea);
        if (likeClicked != true) {
            window.alert("Error: message, or id is not valid");
            return;
        }

        // set up an AJAX PUT.
        // When the server replies, the result will go to onSubmitResponse
        const doAjax = async () => {
            await fetch(`${backendUrl}/ideas/${id}`, {
                method: 'PUT',
                body: JSON.stringify({
                    mLikeIncrement: like
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then((response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    // return response.json();
                    return Promise.resolve(response.json());
                }
                // Otherwise, handle server errors with a detailed popup message
                else {
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                // return response;
                return Promise.reject(response);
            }).then((data) => {
                editEntryForm.onSubmitResponse(data);
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error, on Ajax PUT (edit)");
            });
        }
        // resetting flag
        likeClicked = false;
        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a 
     * result.
     * 
     * @param data The object returned by the server
     */
    private onSubmitResponse(data: any) {
        // If we get an "ok" message, clear the form and refresh the main 
        // listing of messages
        if (data.mStatus === "ok") {
            //editEntryForm.clearForm();
            mainList.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error, on submit response (edit)");
        }
    }
} // end class EditEntryForm

// Run some configuration code when the web page loads
document.addEventListener('DOMContentLoaded', () => {
    (<HTMLElement>document.getElementById("addElement")).style.display = "none";
    (<HTMLElement>document.getElementById("showElements")).style.display = "block";
    // having add show when clicked
    document.getElementById("addButtonFooter")?.addEventListener("click", (e) =>{
        (<HTMLElement>document.getElementById("addElement")).style.display = "block";
        (<HTMLElement>document.getElementById("showElements")).style.display = "none";
    });
    // Create the object that controls the "New Entry" form
    newEntryForm = new NewEntryForm();
    // Create the object for the main data list, and populate it with data from the server
    mainList = new ElementList();
    // Create the object that controls the "Edit Entry" form
    editEntryForm = new EditEntryForm();
    mainList.refresh();
}, false);
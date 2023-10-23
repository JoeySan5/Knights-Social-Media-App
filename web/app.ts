/**
 * a lot of code and comments was taken from tutorials like both web fornt ends as well as CORS tutorial 
*/ 


/**
 * Prevent compiler errors when using jQuery.  "$" will be given a type of 
 * "any", so that we can use it anywhere, and assume it has any fields or
 * methods, without the compiler producing an error.
*/
var $: any;

/**
 * The 'this' keyword does not behave in JavaScript/TypeScript like it does in
 * Java.  Since there is only one NewEntryForm, we will save it to a global, so
 * that we can reference it from methods of the NewEntryForm in situations where
 * 'this' won't work correctly.
*/ 
var newEntryForm: NewEntryForm;

/**
 * a global for the main ElementList of the program.  See newEntryForm for 
 * explanation
*/ 
var mainList: ElementList;


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
        // event listeners for adding a post and canceling
        document.getElementById("addCancel")?.addEventListener("click", (e) => { newEntryForm.clearForm(); });
        document.getElementById("addButton")?.addEventListener("click", (e) => { newEntryForm.submitForm(); });

    }

    /**
     * Clear the form's input fields
     */
    clearForm() {
        (<HTMLInputElement>document.getElementById("newIdea")).value = "";

        // reset the UI
        (<HTMLElement>document.getElementById("addElement")).style.display = "none";
        (<HTMLElement>document.getElementById("ideaList")).style.display = "block";
    }

    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitForm() {
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
                console.log(data);
            }).catch((error) => {
                console.warn('Something went wrong with GET.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    /**
     * update method that builds the table displaying the list of ideas
     * @param data the object used to build the table (ideas)
     */
    private update(data: any) {
        let elem_ideaList = document.getElementById("ideaList");

        if (elem_ideaList !== null) {
            elem_ideaList.innerHTML = "";
            // table that contains all of the ideas
            let fragment = document.createDocumentFragment();
            let table = document.createElement('table');
            table.setAttribute('id', 'ideaTable');
            for (let i = 0; i < data.mData.length; ++i) {
                let tr = document.createElement('tr');
                let td_message = document.createElement('td');
                let td_like = document.createElement('td');
                td_message.innerHTML = data.mData[i].mContent;
                td_like.innerHTML = data.mData[i].mLikeCount;
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
            all_delbtns[i].setAttribute('id', 'deleteId');
            all_delbtns[i].addEventListener("click", (e) => { mainList.clickDelete(e); });
        }

        // Find all of the like buttons, and set their behavior
        const all_likebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("likebtn"));
        for (let i = 0; i < all_likebtns.length; ++i) {
            all_likebtns[i].setAttribute('id', 'likeId');
            all_likebtns[i].addEventListener("click", (e) => { mainList.addLike(e); });
        }
        // Find all of the dislike buttons, and set their behavior
        const all_dislikebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("dislikebtn"));
        for (let i = 0; i < all_dislikebtns.length; ++i) {
            all_dislikebtns[i].setAttribute('id', 'dislikeId');
            all_dislikebtns[i].addEventListener("click", (e) => { mainList.addDisLike(e); });
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
                // refresh after each delete
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
     * addLike is the code we run in response to a click of a like button
     */
    private addLike(e: Event) {
        console.log("addLike");
        // as in clickDelete, we need the ID of the row
        const id = (<HTMLElement>e.target).getAttribute("data-value");
        // Issue an AJAX GET and then pass the result to editEntryForm.init()
        const doAjax = async () => {
            await fetch(`${backendUrl}/ideas/${id}`, {
                method: 'PUT',
                body: JSON.stringify({
                    mLikeIncrement: 1
                }),
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
                console.log(data);
                mainList.refresh();
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error, on click edit");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);

    }

/**
 * addDisLike is the code we run in response to a click of a dislike button
 */
    private addDisLike(e: Event) {
        console.log("disLike");
        // as in clickDelete, we need the ID of the row
        const id = (<HTMLElement>e.target).getAttribute("data-value");
        // Issue an AJAX GET and then pass the result to editEntryForm.init()
        const doAjax = async () => {
            await fetch(`${backendUrl}/ideas/${id}`, {
                method: 'PUT',
                body: JSON.stringify({
                    mLikeIncrement: -1
                }),
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
                console.log(data);
                mainList.refresh();
            }).catch((error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error, on click edit");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }



    /**
     * likeButtons() adds a button to the HTML for each row
     * adds like and dislike buttons 
     * gets id of each row 
     * @param id is the id of the idea
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

 
    /**
     * deleteButtons() adds a button to the HTML for each row
     * adds like and dislike buttons 
     * gets id of each row 
     * @param id is the id of the idea
     */
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
 * configurations for when the page loads
 * initially shows idea list and does not make add element visible
 * controls when add post6 is clicked reverses intialy configuiration
 */
document.addEventListener('DOMContentLoaded', () => {
    // display idea lists, block add element
    (<HTMLElement>document.getElementById("addElement")).style.display = "none";
    (<HTMLElement>document.getElementById("ideaList")).style.display = "block";
    // having add feature show when clicked, block all ideas list
    document.getElementById("addButtonFooter")?.addEventListener("click", (e) => {
        (<HTMLElement>document.getElementById("addElement")).style.display = "block";
        (<HTMLElement>document.getElementById("ideaList")).style.display = "none";
    });
    // Create the object that controls the "New Entry" form
    newEntryForm = new NewEntryForm();
    // Create the object for the main data list, and populate it with data from the server
    mainList = new ElementList();
    // Create the object that controls the "Edit Entry" form
    mainList.refresh();
}, false);
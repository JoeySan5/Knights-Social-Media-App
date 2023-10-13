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

// global for the backend url
const backendUrl = "https://team-knights.dokku.cse.lehigh.edu/";

/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */
class NewEntryForm {
    /**
     * To initialize the object, we say what method of NewEntryForm should be
     * run in response to each of the form's buttons being clicked.
     */
    constructor() {
        document.getElementById("addButton")?.addEventListener("click", (e) => {newEntryForm.clearForm();});
        
    }

    /**
     * Clear the form's input fields
     */
    clearForm() {
        (<HTMLInputElement>document.getElementById("newIdea")).value = "";
    }

    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    submitForm() {
        window.alert("Submit form called.");
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
            await fetch(`${backendUrl}/`, {
                method: 'POST',
                body: JSON.stringify({
                    Idea: idea
                }),
                headers: {
                    'Content-type': 'application/json; charset=UTF-8'
                }
            }).then( (response) => {
                // If we get an "ok" message, return the json
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup message
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                newEntryForm.onSubmitResponse(data);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
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
            newEntryForm.clearForm();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.ideas);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
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
            }).then( (response) => {
                // If we get an "ok" idea, clear the form
                if (response.ok) {
                    return Promise.resolve( response.json() );
                }
                // Otherwise, handle server errors with a detailed popup idea
                else{
                    window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
                }
                return Promise.reject(response);
            }).then( (data) => {
                mainList.update(data);
                console.log(data);
            }).catch( (error) => {
                console.warn('Something went wrong.', error);
                window.alert("Unspecified error");
            });
        }

        // make the AJAX post and output value or error message to console
        doAjax().then(console.log).catch(console.log);
    }

    private update(data: any) {
        let elem_ideaList = document.getElementById("ideaList");

        if(elem_ideaList !== null) {
            elem_ideaList.innerHTML = "";

            let fragment = document.createDocumentFragment();
            let table = document.createElement('table');

            for (let i = 0; i < data.mData.length; ++i) {
                let tr = document.createElement('tr');
                let td_id = document.createElement('td');
                td_id.innerHTML = data.mData[i].mId;
                tr.appendChild(td_id);
                tr.appendChild( this.buttons(data.mData[i].mId) );
                table.appendChild(tr);
            }
            fragment.appendChild(table);

            elem_ideaList.appendChild(fragment);
        }       
    }

    /**
     * buttons() adds a 'delete' button to the HTML for each row
     *
     * doesn't do anything yet
     */
    private buttons(id: string): DocumentFragment {
        return document.createDocumentFragment();
    }
} // end class ElementList

// Run some configuration code when the web page loads
document.addEventListener('DOMContentLoaded', () => {
    // Create the object that controls the "New Entry" form
    newEntryForm = new NewEntryForm();
    // Create the object for the main data list, and populate it with data from the server
    mainList = new ElementList();
    mainList.refresh();
    window.alert('DOMContentLoaded');
}, false);
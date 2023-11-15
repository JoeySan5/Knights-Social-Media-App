import { Component } from '@angular/core';


var mainList: IdeaListComponent;
var $: any;
const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";
const sehyounKey = "k0kyOGwPlod5";

@Component({
  selector: 'idea-list',
  templateUrl: './idea-list.component.html',
  styleUrls: ['./idea-list.component.css']
})
export class IdeaListComponent {

    constructor(){
        this.refresh();
    }
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
                console.log("recieved response from server for get");
                return Promise.resolve(response.json());

            }
            // Otherwise, handle server errors with a detailed popup idea
            else {
                window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
            }
            return Promise.reject(response);
        }).then((data) => {
            this.update(data);
            console.log('here is data:', data);
        }).catch((error) => {
            console.warn('Something went wrong with GET.', error);
            console.log("Unspecified error with refresh()");
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
    console.log('in update')
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
        all_delbtns[i].addEventListener("click", (e) => { this.clickDelete(e); });
    }

    // Find all of the like buttons, and set their behavior
    const all_likebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("likebtn"));
    for (let i = 0; i < all_likebtns.length; ++i) {
        all_likebtns[i].setAttribute('id', 'likeId');
        all_likebtns[i].addEventListener("click", (e) => { this.addLike(e); });
    }
    // Find all of the dislike buttons, and set their behavior
    const all_dislikebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("dislikebtn"));
    for (let i = 0; i < all_dislikebtns.length; ++i) {
        all_dislikebtns[i].setAttribute('id', 'dislikeId');
        all_dislikebtns[i].addEventListener("click", (e) => { this.addDisLike(e); });
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
            this.refresh();
            console.log(data);
        }).catch((error) => {
            console.warn('Something went wrong.', error);
            window.alert("Unspecified error, in clickDelete function");
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
        await fetch(`${backendUrl}/ideas/${id}/likes`, {
            method: 'PUT',
            body: JSON.stringify({
                sessionKey: sehyounKey,
                value: 1
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
            this.refresh();
        }).catch((error) => {
            console.warn('Something went wrong.', error);
            window.alert("Unspecified error, on addLike");
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
        await fetch(`${backendUrl}/ideas/${id}/likes`, {
            method: 'PUT',
            body: JSON.stringify({
                sessionKey: sehyounKey,
                value: -1
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
            this.refresh();
        }).catch((error) => {
            console.warn('Something went wrong.', error);
            window.alert("Unspecified error, on addDislike");
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
}

import { Component, OnInit } from '@angular/core';
import { DetailedPostInfoService } from '../detailed-post-info.service';
import { Router } from '@angular/router';



var mainList: IdeaListComponent;
var $: any;
// const backendUrl = "https://team-knights.dokku.cse.lehigh.edu";
const backendUrl = "http://localhost:8998";

const sessionKey = localStorage.getItem('sessionKey');



@Component({
  selector: 'idea-list',
  templateUrl: './idea-list.component.html',
  styleUrls: ['./idea-list.component.css']
})
export class IdeaListComponent implements OnInit{
    private data: any;

    
    //this constructor id's and injection site for the service. setting detailedPost... as an instance of DetailedPost...
    constructor(private detailedPostInfoService: DetailedPostInfoService, private router: Router){
    }

    ngOnInit(){
        this.refresh();
    }
  /**
     * refresh is the public method for updating ideaList
     */
  refresh() {
    // Issue an AJAX GET and then pass the result to update(). 
    const doAjax = async () => {
        await fetch(`${backendUrl}/ideas?sessionKey=${sessionKey}`, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json; charset=UTF-8'
            },
            cache: 'no-cache'
        }).then( (response) => {
            // If we get an "ok" idea, clear the form
            if (response.ok) {
                console.log("recieved 'ok' from server in refresh()");
                //Essentially, a promise is a returned object to which you attach callbacks, instead of passing callbacks into a function
                //whatever is returned is passed onto the next 'then()'
                //resolve() method 'resolves' a given value to a promise. Returns a resolved promise indicating resolution of await/async
                return Promise.resolve(response.json());

            }
            // Otherwise, handle server errors with a detailed popup idea
            else {
                window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
            }
            //opposite of resolve(). reject() throws an error when reached here
            return Promise.reject(response);
        }).then((data) => {
            console.log('here is data:', data);
            this.update(data);
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


    //THE JOB IS TO NOW MAKE TR CLICKABLE SO THAT WE CAN ACCESS THE FULL DETAILED PAGE VIEW
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
            let user_name = document.createElement('td')
            user_name.className = "usern";

            td_message.innerHTML = data.mData[i].mContent;
            td_like.innerHTML = data.mData[i].mLikeCount;
            user_name.innerHTML = data.mData[i].mPosterUsername;
            td_message.style.cursor = 'pointer';

            tr.appendChild(user_name);
            tr.appendChild(td_message);
            tr.appendChild(this.likeButtons(data.mData[i].mId));
            tr.appendChild(td_like);

            tr.appendChild(this.deleteButtons(data.mData[i].mId));
            table.appendChild(tr);
            tr.addEventListener("click", (e) => { this.trClicK(data.mData[i].mId) });
            user_name.addEventListener("click", (e) => { 
                e.stopPropagation();
                this.userNClick(data.mData[i].mUserId); });
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
        all_likebtns[i].addEventListener("click", (e) => { 
            e.stopPropagation();

            this.addLike(e); });
    }
    // Find all of the dislike buttons, and set their behavior
    const all_dislikebtns = (<HTMLCollectionOf<HTMLInputElement>>document.getElementsByClassName("dislikebtn"));
    for (let i = 0; i < all_dislikebtns.length; ++i) {
        all_dislikebtns[i].setAttribute('id', 'dislikeId');
        all_dislikebtns[i].addEventListener("click", (e) => { 
            e.stopPropagation();
            this.addDisLike(e); });
    }
}

private userNClick(userId: String){
    
  //  curl -s http://localhost:8998/users/101136375578726959533?sessionKey=String -X GET
  const doAjax = async () => {
    await fetch(`${backendUrl}/users/${userId}?sessionKey=${sessionKey}`, {
        method: 'GET',
        headers: {
            'Content-type': 'application/json; charset=UTF-8'
        },
        cache: 'no-cache'
    }).then( (response) => {
        // If we get an "ok" idea, clear the form
        if (response.ok) {
            console.log("recieved 'ok' from server in trClick()");
            //Essentially, a promise is a returned object to which you attach callbacks, instead of passing callbacks into a function
            //whatever is returned is passed onto the next 'then()'
            //resolve() method 'resolves' a given value to a promise. Returns a resolved promise indicating resolution of await/async
            return Promise.resolve(response.json());

        }
        // Otherwise, handle server errors with a detailed popup idea
        else {
            window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
        }
        //opposite of resolve(). reject() throws an error when reached here
        return Promise.reject(response);
    }).then((data) => {
        
        console.log('here is data:', data);
        this.data = data;
        this.detailedPostInfoService.setData(this.data.mData);
        this.router.navigate(["/other-profile"]);
                //this.update(data);
    }).catch((error) => {
        console.warn('Something went wrong with GET.', error);
        console.log("Unspecified error with trClick()");
    });
}

// make the AJAX post and output value or error message to console
doAjax().then(console.log).catch(console.log);

}

private trClicK(Id: String){
    const doAjax = async () => {
        await fetch(`${backendUrl}/ideas/${Id}?sessionKey=${sessionKey}`, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json; charset=UTF-8'
            },
            cache: 'no-cache'

        }).then( (response) => {
            // If we get an "ok" idea, clear the form
            if (response.ok) {
                console.log("recieved 'ok' from server in trClick()");
                //Essentially, a promise is a returned object to which you attach callbacks, instead of passing callbacks into a function
                //whatever is returned is passed onto the next 'then()'
                //resolve() method 'resolves' a given value to a promise. Returns a resolved promise indicating resolution of await/async
                return Promise.resolve(response.json());

            }
            // Otherwise, handle server errors with a detailed popup idea
            else {
                window.alert(`The server replied not ok: ${response.status}\n` + response.statusText);
            }
            //opposite of resolve(). reject() throws an error when reached here
            return Promise.reject(response);
        }).then((data) => {
            
            console.log('here is data:', data);
            this.data = data;
            this.detailedPostInfoService.setData(this.data.mData);
            this.router.navigate(["/detailed-post"]);
            //this.update(data);
        }).catch((error) => {
            console.warn('Something went wrong with GET.', error);
            console.log("Unspecified error with trClick()");
        });
    }

    // make the AJAX post and output value or error message to console
    doAjax().then(console.log).catch(console.log);
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
                sessionKey: sessionKey,
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
                sessionKey: sessionKey,
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
    btn.style.cursor = 'pointer';
    td.appendChild(btn);
    fragment.appendChild(td);

    // create dislike button
    btn = document.createElement('button');
    btn.classList.add("dislikebtn");
    btn.setAttribute('data-value', id);
    btn.innerHTML = 'dislike';
    btn.style.cursor = 'pointer';
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
    btn.style.cursor = 'pointer';
    btn.addEventListener('click', (e) => {
        e.stopPropagation();
    });

    td.appendChild(btn);
    fragment.appendChild(td);

    return fragment;
}
}

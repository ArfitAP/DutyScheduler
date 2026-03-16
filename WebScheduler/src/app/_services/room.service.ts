import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppSettings } from './app.settings';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  private baseUrl = AppSettings.API_ENDPOINT + 'rooms';

  constructor(private http: HttpClient) { }

  getMyRooms(): Observable<any[]> {
    return this.http.get(this.baseUrl + '/my', { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  createRoom(name: string, description: string): Observable<any> {
    return this.http.post(this.baseUrl, { name, description }, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  getRoomDetail(roomId: number): Observable<any> {
    return this.http.get(this.baseUrl + '/' + roomId, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  updateRoom(roomId: number, name: string, description: string): Observable<any> {
    return this.http.put(this.baseUrl + '/' + roomId, { name, description }, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  deleteRoom(roomId: number): Observable<any> {
    return this.http.delete(this.baseUrl + '/' + roomId, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  inviteUser(roomId: number, userId: number): Observable<any> {
    return this.http.post(this.baseUrl + '/' + roomId + '/invite/' + userId, {}, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  removeMember(roomId: number, userId: number): Observable<any> {
    return this.http.delete(this.baseUrl + '/' + roomId + '/members/' + userId, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  getPendingInvitations(): Observable<any[]> {
    return this.http.get(this.baseUrl + '/invitations', { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  acceptInvitation(invitationId: number): Observable<any> {
    return this.http.post(this.baseUrl + '/invitations/' + invitationId + '/accept', {}, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  declineInvitation(invitationId: number): Observable<any> {
    return this.http.post(this.baseUrl + '/invitations/' + invitationId + '/decline', {}, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get(this.baseUrl + '/users', { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  getDayHours(roomId: number, month: string): Observable<any[]> {
    return this.http.get(this.baseUrl + '/' + roomId + '/day-hours/' + month, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  setDayHours(roomId: number, month: string, hours: any[]): Observable<any> {
    return this.http.post(this.baseUrl + '/' + roomId + '/day-hours/' + month, hours, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  // Schedule endpoints (room-scoped)
  getSchedule(roomId: number, month: string): Observable<any> {
    return this.http.get(AppSettings.API_ENDPOINT + 'schedule/rooms/' + roomId + '/schedule/' + month, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  getUserApplications(roomId: number, userId: number, month: string): Observable<any> {
    return this.http.get(AppSettings.API_ENDPOINT + 'schedule/rooms/' + roomId + '/userapplications/' + userId + '/' + month, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  saveUserApplications(roomId: number, userApplication: any): Observable<any> {
    return this.http.post(AppSettings.API_ENDPOINT + 'schedule/rooms/' + roomId + '/adduserapplications', userApplication, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  getUsersForActivation(roomId: number, month: string): Observable<any[]> {
    return this.http.get(AppSettings.API_ENDPOINT + 'schedule/rooms/' + roomId + '/usersforactivation/' + month, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  saveUserActives(roomId: number, month: string, userId: number, userActives: any[]): Observable<any> {
    return this.http.post(AppSettings.API_ENDPOINT + 'schedule/rooms/' + roomId + '/saveUserActivesInMonth/' + month + '/' + userId, userActives, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  generateSchedule(roomId: number, month: string, userId: number): Observable<any> {
    return this.http.get(AppSettings.API_ENDPOINT + 'schedule/rooms/' + roomId + '/generateSchedule/' + month + '/' + userId, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  isServerBusy(roomId: number, month: string): Observable<boolean> {
    return this.http.get(AppSettings.API_ENDPOINT + 'schedule/rooms/' + roomId + '/isServerBusy/' + month, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }

  getHolydays(month: string): Observable<any[]> {
    return this.http.get(AppSettings.API_ENDPOINT + 'schedule/getHolydaysForMonth/' + month, { responseType: 'text' }).pipe(map(data => JSON.parse(data)));
  }
}

//
//  FanClubHome.swift
//  MovieSwift
//
//  Created by Thomas Ricouard on 24/07/2019.
//  Copyright © 2019 Thomas Ricouard. All rights reserved.
//

import SwiftUI
import common

struct FanClubHome: ConnectedView {
    struct Props {
        let peoples: [String]
        let popular: [String]
        let dispatch: DispatchFunction
    }
    
    @State private var currentPage = 1
    
    func map(state: AppState , dispatch: @escaping DispatchFunction) -> Props {
        Props(peoples: state.peoplesState.fanClub.map{ $0 }.sorted(),
              popular: state.peoplesState.popular.filter{ !state.peoplesState.fanClub.contains($0) },
              dispatch: dispatch)
    }
    
    func body(props: Props) -> some View {
        NavigationView {
            List {
                Section {
                    ForEach(props.peoples, id: \.self) { people in
                        NavigationLink(destination: PeopleDetail(peopleId: people)) {
                            PeopleRow(peopleId: people)
                        }
                    }.onDelete(perform: { index in
                        props.dispatch(PeopleActions.RemoveFromFanClub(people: props.peoples[index.first!]))
                    })
                }
            
                Section(header: Text("Popular people to add to your Fan Club")) {
                    ForEach(props.popular, id: \.self) { people in
                        NavigationLink(destination: PeopleDetail(peopleId: people)) {
                            PeopleRow(peopleId: people)
                        }
                    }
                }
                
                if !props.popular.isEmpty {
                    Rectangle()
                        .foregroundColor(.clear)
                        .onAppear {
                            self.currentPage += 1
                            props.dispatch(peopleActions.fetchPopular(page: Int32(self.currentPage)))
                    }
                }
            }
            .navigationBarTitle("Fan Club")
        }
        .onAppear {
            props.dispatch(peopleActions.fetchPopular(page: Int32(self.currentPage)))
        }
    }
}

#if DEBUG
struct FanClubHome_Previews: PreviewProvider {
    static var previews: some View {
        FanClubHome()
    }
}
#endif
